package com.uin.netty.nativeio.zerocopy;

import java.io.*;
import java.net.Socket;
import lombok.extern.slf4j.*;

/**
 * 传统 copy
 */
@Slf4j
public class FileCopyClient {

  public static void main(String[] args) {
    String serverIp = "127.0.0.1";
    int port = 8888;
    String filePath = "pm_sys_calendar_detail(2).xlsx";  // 待发送的文件

    try (Socket socket = new Socket(serverIp, port);
        OutputStream out = socket.getOutputStream();
        FileInputStream fis = new FileInputStream(filePath)) {

      long startTime = System.currentTimeMillis();

      byte[] buffer = new byte[4096];
      int bytesRead;
      while ((bytesRead = fis.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
      }

      long endTime = System.currentTimeMillis();
      log.info("文件发送完成。耗时：{} ms", (endTime - startTime));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
