package com.uin.netty.nativeio.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * NIO
 */
public class NonBlockingIOExample {


  private static boolean stop = false;

  /**
   * 主函数，用于演示使用Selector和SocketChannel进行非阻塞网络通信。 该函数创建了一个选择器和一个非阻塞套接字通道，注册到选择器上，并尝试连接到example.com的80端口。
   * 在循环中，它使用选择器来监听通道的连接、读和写事件，并根据事件类型执行相应的操作。
   *
   * @param args 命令行参数，未使用。
   */
  public static void main(String[] args) {
    try {
      // 打开一个选择器
      Selector selector = Selector.open();
      // 打开一个套接字通道
      SocketChannel socketChannel = SocketChannel.open();
      // 配置套接字通道为非阻塞模式
      socketChannel.configureBlocking(false);
      // 尝试连接到example.com的80端口
      socketChannel.connect(new InetSocketAddress("example.com", 80));
      // 注册套接字通道到选择器上，感兴趣的事件为连接、读和写
      socketChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);

      // 无限循环，监听选择器上的事件
      while (!stop) {
        // 阻塞等待直到有就绪的事件发生
        selector.select();
        // 获取所有就绪的事件
        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

        // 遍历所有就绪的事件
        while (keyIterator.hasNext()) {
          SelectionKey key = keyIterator.next();
          keyIterator.remove();

          // 如果事件是连接事件
          if (key.isConnectable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            // 完成连接过程
            channel.finishConnect();
          } else if (key.isReadable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            // 分配一个缓冲区用于读取数据
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            // 从通道读取数据到缓冲区
            channel.read(buffer);
            // 写模式 转读模式
            buffer.flip();
            // 处理读取的数据
          } else if (key.isWritable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            // 将要发送的数据放入缓冲区
            ByteBuffer buffer = ByteBuffer.wrap("Hello, Server!".getBytes());
            // 将缓冲区的数据写入通道
            channel.write(buffer);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
