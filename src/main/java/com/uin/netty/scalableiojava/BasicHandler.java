package com.uin.netty.scalableiojava;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;

/**
 * 单线程基本处理器，I/O 的读写以及业务的处理均由 Reactor 线程完成
 *
 * @see Reactor
 */
public class BasicHandler implements Runnable {

  private static final int MAXIN = 1024;
  private static final int MAXOUT = 1024;

  public SocketChannel socketChannel;

  public SelectionKey selectionKey;

  ByteBuffer input = ByteBuffer.allocate(MAXIN);
  ByteBuffer output = ByteBuffer.allocate(MAXOUT);

  // 定义服务的逻辑状态

  static final int READING = 0, SENDING = 1, CLOSED = 2;
  int state = READING;

  public BasicHandler(Selector selector, SocketChannel socketChannel) throws IOException {
    this.socketChannel = socketChannel;
    socketChannel.configureBlocking(false);
    // Optionally try first read now
    // 注册通道
    selectionKey = socketChannel.register(selector, READING);
    // 绑定要处理的事件
    selectionKey.interestOps(SelectionKey.OP_READ);
    selectionKey.attach(this); //管理事件的处理程序
    selector.wakeup(); // 唤醒 select() 方法
  }

  public BasicHandler(SocketChannel sc) {
    socketChannel = sc;
  }


  @Override
  public void run() {
    try {
      if (state == READING) {
        // 此时通道已经准备好读取字节
        read();
      } else if (state == SENDING) {
        // 此时通道已经准备好写入字节
        send();
      }
    } catch (IOException e) {
      // 关闭连接
      try {
        selectionKey.channel().close();
      } catch (IOException ignore) {
      }
    }
  }

  protected void read() throws IOException {
    // 清空接受缓冲区
    input.clear();
    int n = socketChannel.read(input);
    // 如果读取了完整的数据
    if (inputIsComplete(n)) {
      process();
      // 待发送的数据已经放入发送缓冲区中
      // 更改服务的逻辑状态以及
      selectionKey.interestOps(SelectionKey.OP_WRITE);
    }
  }

  // 缓存每次读取的内容
  StringBuilder request = new StringBuilder();

  /**
   * 当读取到 \r\n 时表示结束
   *
   * @param bytes 读取的字节数，-1 通常是连接被关闭，0 非阻塞模式可能返回
   * @throws IOException
   */
  protected boolean inputIsComplete(int bytes) throws IOException {
    if (bytes > 0) {
      input.flip(); // 切换成读取模式
      while (input.hasRemaining()) {
        byte ch = input.get();

        if (ch == 3) { // ctrl+c 关闭连接
          state = CLOSED;
          return true;
        } else if (ch == '\r') { // continue
        } else if (ch == '\n') {
          // 读取到了 \r\n 读取结束
          state = SENDING;
          return true;
        } else {
          request.append((char) ch);
        }
      }
    } else if (bytes == -1) {
      // -1 客户端关闭了连接
      throw new EOFException();
    } else {
    } // bytes == 0 继续读取
    return false;
  }

  /**
   * 根据业务处理结果，判断如何响应
   *
   * @throws EOFException 用户输入 ctrl+c 主动关闭
   */
  protected void process() throws EOFException {
    if (state == CLOSED) {
      throw new EOFException();
    } else if (state == SENDING) {
      String requestContent = request.toString(); // 请求内容
      byte[] response = requestContent.getBytes(StandardCharsets.UTF_8);
      output.put(response);
    }
  }

  protected void send() throws IOException {
    int written = -1;
    output.flip();// 切换到读取模式，判断是否有数据要发送
    if (output.hasRemaining()) {
      written = socketChannel.write(output);
    }

    // 检查连接是否处理完毕，是否断开连接
    if (outputIsComplete(written)) {
      selectionKey.channel().close();
    } else {
      // 否则继续读取
      state = READING;
      // 把提示发到界面
      socketChannel.write(ByteBuffer.wrap("\r\nreactor> ".getBytes()));
      selectionKey.interestOps(SelectionKey.OP_READ);
    }

  }

  /**
   * 当用户输入了一个空行，表示连接可以关闭了
   */
  protected boolean outputIsComplete(int written) {
    if (written <= 0) {
      // 用户只敲了个回车， 断开连接
      return true;
    }

    // 清空旧数据，接着处理后续的请求
    output.clear();
    request.delete(0, request.length());
    return false;
  }
}
