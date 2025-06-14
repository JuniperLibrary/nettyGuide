package com.uin.netty.nativeio.nio.selector;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class NIOClient {

  public static void main(String[] args) throws IOException {
    //得到一个网络通道
    SocketChannel socketChannel = SocketChannel.open();
    //非阻塞
    socketChannel.configureBlocking(false);
    //提供服务器的ip和端口
    InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);
    //链接服务器
    if (!socketChannel.connect(inetSocketAddress)) {
      while (!socketChannel.finishConnect()) {
        log.info("链接需要时间，客户端不会阻塞");
      }
    }
    //String str = "hello world!";
    Scanner scanner = new Scanner(System.in);
    String str;
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    while (scanner.hasNext()) {
      str = scanner.next();
      System.out.println(str);
      buffer.put(str.getBytes());
      buffer.flip();
      socketChannel.write(buffer);
      buffer.clear();
    }
    //生成一个和数组一样大的buffer
//        ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes());
//        //发送数据，将buffer写入channel
//        socketChannel.write(byteBuffer);

    //代码会停在这
    System.in.read();
  }
}
