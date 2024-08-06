package com.uin.netty.nativeio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * IO多路复用
 */
public class MultiplexingIOExample {

  private static boolean stop = false;

  /**
   * 主函数，用于启动一个非阻塞模式的简单回显服务器。
   *
   * @param args 命令行参数，在此示例中未使用
   */
  public static void main(String[] args) {
    try {
      // 创建ServerSocketChannel，并绑定到本地的8080端口
      ServerSocketChannel serverChannels = ServerSocketChannel.open();
      serverChannels.socket().bind(new InetSocketAddress(8080));
      serverChannels.configureBlocking(false); // 设置ServerSocketChannel为非阻塞模式

      // 创建Selector，用于多路复用I/O操作
      Selector selector = Selector.open();

      // 将ServerSocketChannel注册到Selector上，监听客户端连接请求
      serverChannels.register(selector, SelectionKey.OP_ACCEPT);

      // 服务器主循环
      while (!stop) {
        selector.select(); // 阻塞等待至少有一个注册的通道上有事件发生

        // 获取所有已就绪的SelectionKey集合
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectedKeys.iterator();

        // 处理每一个就绪的SelectionKey
        while (iterator.hasNext()) {
          SelectionKey key = iterator.next();
          iterator.remove(); // 移除当前SelectionKey，防止重复处理

          if (key.isAcceptable()) {
            // 处理新的客户端连接请求
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel clientChannel = server.accept();
            clientChannel.configureBlocking(false); // 设置客户端SocketChannel为非阻塞模式
            clientChannel.register(selector, SelectionKey.OP_READ); // 注册客户端SocketChannel到Selector上，监听可读事件

          } else if (key.isReadable()) {
            // 处理客户端发送的数据
            SocketChannel clientChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024); // 创建ByteBuffer用于接收数据
            int bytesRead = clientChannel.read(buffer); // 从客户端读取数据到ByteBuffer

            if (bytesRead == -1) {
              // 客户端关闭了连接
              clientChannel.close();
            } else {
              // 数据接收完成，将缓冲区翻转准备写入
              buffer.flip();
              clientChannel.write(buffer); // 将接收到的数据回显给客户端
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
