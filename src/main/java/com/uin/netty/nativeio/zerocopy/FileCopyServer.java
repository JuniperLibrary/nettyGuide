package com.uin.netty.nativeio.zerocopy;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import lombok.extern.slf4j.*;

/**
 * 传统 copy
 */
@Slf4j
public class FileCopyServer {

  public static void main(String[] args) {
    int port = 8888;
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      log.info("服务器启动，等待连接...");

      Socket clientSocket = serverSocket.accept();
      log.info("客户端已连接：{}", clientSocket.getInetAddress());

      long startTime = System.currentTimeMillis();

      try (InputStream in = clientSocket.getInputStream();
          FileOutputStream fos = new FileOutputStream("pm_sys_calendar_detail.xlsx")) {

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
          fos.write(buffer, 0, bytesRead);
        }

        long endTime = System.currentTimeMillis();
        log.info("文件接收完成。耗时：{} ms", (endTime - startTime));
      }

      clientSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

