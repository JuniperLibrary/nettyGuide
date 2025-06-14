package com.uin.netty.nativeio.zerocopy.niocopy;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import lombok.extern.slf4j.*;

@Slf4j
public class NioZeroCopyClient {

  public static void main(String[] args) throws IOException {
    String serverIp = "127.0.0.1";
    int port = 8888;
    String filePath = "pm_sys_calendar_detail(2).xlsx"; // 待发送的文件

    // 创建 SocketChannel 并连接到服务器
    SocketChannel socketChannel = SocketChannel.open();
    socketChannel.connect(new InetSocketAddress(serverIp, port));

    // 打开文件输入通道
    FileChannel fileChannel = new FileInputStream(filePath).getChannel();

    long startTime = System.currentTimeMillis();

    long size = fileChannel.size(); // 获取文件大小
    long position = 0;

    // 使用 transferTo 实现零拷贝传输
    while (position < size) {
      long transferred = fileChannel.transferTo(position, size - position, socketChannel);
      position += transferred; // 更新已传输字节数
    }

    long endTime = System.currentTimeMillis();
    log.info("发送完成，耗时：{} ms", (endTime - startTime));

    // 关闭资源
    fileChannel.close();
    socketChannel.close();
  }
}
