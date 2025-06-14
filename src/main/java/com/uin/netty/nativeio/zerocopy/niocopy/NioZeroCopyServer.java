package com.uin.netty.nativeio.zerocopy.niocopy;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import lombok.extern.slf4j.*;

@Slf4j
public class NioZeroCopyServer {

  public static void main(String[] args) throws IOException {
    int port = 8888;

    // 创建 ServerSocketChannel，并绑定端口
    ServerSocketChannel serverChannel = ServerSocketChannel.open();
    serverChannel.bind(new InetSocketAddress(port));
    log.info("服务端启动，监听端口：{}", port);

    while (true) {
      // 等待客户端连接（阻塞）
      SocketChannel client = serverChannel.accept();
      log.info("客户端已连接：{}", client.getRemoteAddress());

      long startTime = System.currentTimeMillis();

      // 创建输出文件通道，准备写入接收到的数据
      FileChannel destChannel = new FileOutputStream("pm_sys_calendar_detail3.xlsx").getChannel();

      // 创建缓冲区用于接收数据
      ByteBuffer buffer = ByteBuffer.allocate(4096);

      // 读取客户端数据并写入文件
      while (client.read(buffer) != -1) {
        buffer.flip(); // 切换为读模式
        destChannel.write(buffer);
        buffer.clear(); // 清空缓冲区，准备下一次读取
      }

      long endTime = System.currentTimeMillis();
      log.info("接收完成，耗时：{} ms", (endTime - startTime));

      // 关闭资源
      destChannel.close();
      client.close();
    }
  }
}
