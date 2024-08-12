package com.uin.netty.nativeinternet.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerThread extends Thread {

  private Socket socket;

  /**
   * 构造函数，初始化ServerThread实例
   *
   * @param socket 客户端Socket对象，用于处理具体的客户端连接
   */
  public ServerThread(Socket socket) {
    this.socket = socket;
  }

  /**
   * 线程的执行逻辑，用于读取客户端发送的消息并发送回显消息
   */
  public void run() {
    try (
        // 获取Socket的输入流，用于读取客户端发送的数据
        InputStream input = socket.getInputStream();
        // 将输入流包装为BufferedReader，用于按行读取文本数据
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        // 获取Socket的输出流，用于向客户端发送数据
        OutputStream output = socket.getOutputStream();
        // 将输出流包装为PrintWriter，用于发送文本数据
        PrintWriter writer = new PrintWriter(output, true)
    ) {
      String text;
      // 循环读取客户端发送的消息，直到没有更多数据
      while ((text = reader.readLine()) != null) {
        log.info("Received: {}", text);
        writer.println("Echo: " + text);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

