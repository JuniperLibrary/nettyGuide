package com.uin.netty.nativeio.nio.buffer;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/**
 * Scattering：将数据写入到 buffer 时，可以采用 buffer 数组，依次写入 [分散] Gathering：从 buffer 读取数据时，可以采用 buffer 数组，依次读
 */
public class ScatteringAndGatheringTest {

  public static void main(String[] args) throws Exception {

    //使用 ServerSocketChannel 和 SocketChannel 网络
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);

    //绑定端口到 socket，并启动
    serverSocketChannel.socket().bind(inetSocketAddress);

    //创建 buffer 数组
    ByteBuffer[] byteBuffers = new ByteBuffer[2];
    byteBuffers[0] = ByteBuffer.allocate(5);
    byteBuffers[1] = ByteBuffer.allocate(3);

    //等客户端连接 (telnet)
    SocketChannel socketChannel = serverSocketChannel.accept();

    int messageLength = 8; //假定从客户端接收 8 个字节

    //循环的读取
    while (true) {
      int byteRead = 0;

      while (byteRead < messageLength) {
        long l = socketChannel.read(byteBuffers);
        byteRead += (int) l; //累计读取的字节数
        System.out.println("byteRead = " + byteRead);
        //使用流打印,看看当前的这个 buffer 的 position 和 limit
        Arrays.stream(byteBuffers).map(buffer -> "position = " + buffer.position() + ", limit = " + buffer.limit())
            .forEach(System.out::println);
      }

      //将所有的 buffer 进行 flip
      Arrays.asList(byteBuffers).forEach(ByteBuffer::flip);
      //将数据读出显示到客户端
      long byteWirte = 0;
      while (byteWirte < messageLength) {
        long l = socketChannel.write(byteBuffers);//
        byteWirte += l;
      }

      //将所有的buffer进行clear
      Arrays.asList(byteBuffers).forEach(ByteBuffer::clear);

      System.out.println("byteRead = " + byteRead + ", byteWrite = " + byteWirte + ", messagelength = " + messageLength);
    }
  }
}
