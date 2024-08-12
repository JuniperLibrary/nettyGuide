package com.uin.netty.nativeinternet.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;

/**
 * TCP客户端示例，用于与TCP服务器建立连接并进行交互
 */
@Slf4j
public class TCPClient {

  /**
   * 主函数，启动客户端并尝试连接到服务器
   *
   * @param args 命令行参数，未使用
   */
  public static void main(String[] args) {
    // 服务器主机名和端口号
    String hostname = "localhost";
    int port = 12345;

    try {
      // 创建Socket连接
      Socket socket = new Socket(hostname, port);

      // 设置输出流和打印写入器，用于向服务器发送数据
      OutputStream output = socket.getOutputStream();
      PrintWriter writer = new PrintWriter(output, true);

      // 设置输入流和缓冲读取器，用于从系统输入读取数据
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

      String text;
      do {
        // 提示用户输入并读取
        text = reader.readLine();
        // 向服务器发送数据
        writer.println(text);

        // 从服务器读取响应
        InputStream input = socket.getInputStream();
        BufferedReader serverReader = new BufferedReader(new InputStreamReader(input));
        String response = serverReader.readLine();
        // 记录服务器的响应
        log.info("Server replied: {}", response);
      } while (!text.equals("bye"));

    } catch (UnknownHostException e) {
      // 无法找到服务器
      log.error("Server not found: {}", e.getMessage());
    } catch (IOException e) {
      // 输入输出异常
      log.error("I/O error: {}", e.getMessage());
    }
  }
}
