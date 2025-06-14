package com.uin.netty.nativeio.zerocopy.niocopy;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;

public class NewIOServer {

  public static void main(String[] args) throws Exception {
    InetSocketAddress address = new InetSocketAddress(7001);
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    ServerSocket serverSocket = serverSocketChannel.socket();
    serverSocket.bind(address);

    //创建buffer
    ByteBuffer byteBuffer = ByteBuffer.allocate(4096);

    while (true) {
      SocketChannel socketChannel = serverSocketChannel.accept();
      int readcount = 0;
      while (-1 != readcount) {
        try {
          readcount = socketChannel.read(byteBuffer);
        } catch (Exception ex) {
          // ex.printStackTrace();
          break;
        }
        //
        byteBuffer.rewind(); //倒带 position = 0 mark 作废
      }
    }
  }
}
