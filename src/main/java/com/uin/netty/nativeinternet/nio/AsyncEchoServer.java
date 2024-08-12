package com.uin.netty.nativeinternet.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class AsyncEchoServer {
  public static void main(String[] args) {
    try {
      Selector selector = Selector.open();
      ServerSocketChannel serverSocket = ServerSocketChannel.open();
      InetSocketAddress hostAddress = new InetSocketAddress("localhost", 5454);
      serverSocket.bind(hostAddress);
      serverSocket.configureBlocking(false);
      serverSocket.register(selector, SelectionKey.OP_ACCEPT);

      System.out.println("Server started...");

      while (true) {
        selector.select();

        Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
        while (keys.hasNext()) {
          SelectionKey key = keys.next();
          keys.remove();

          if (!key.isValid()) {
            continue;
          }

          if (key.isAcceptable()) {
            accept(key);
          } else if (key.isReadable()) {
            read(key);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void accept(SelectionKey key) throws IOException {
    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
    SocketChannel socketChannel = serverSocketChannel.accept();
    socketChannel.configureBlocking(false);
    socketChannel.register(key.selector(), SelectionKey.OP_READ);
  }

  private static void read(SelectionKey key) throws IOException {
    SocketChannel socketChannel = (SocketChannel) key.channel();
    ByteBuffer buffer = ByteBuffer.allocate(256);
    int bytesRead = socketChannel.read(buffer);

    if (bytesRead == -1) {
      socketChannel.close();
      return;
    }

    String result = new String(buffer.array()).trim();
    System.out.println("Message received: " + result);

    buffer.flip();
    socketChannel.write(buffer);
    buffer.clear();
  }
}
