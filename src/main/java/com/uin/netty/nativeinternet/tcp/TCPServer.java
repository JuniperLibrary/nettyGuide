package com.uin.netty.nativeinternet.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于TCP的Socket通信
 */
@Slf4j
public class TCPServer {

  /**
   * 主函数入口
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    // 创建ServerSocket并绑定到12345端口
    try (ServerSocket serverSocket = new ServerSocket(12345)) {
      // 服务器启动日志
      log.info("Server is listening on port 12345");
      // 无限循环，等待客户端连接
      while (true) {
        // 接受一个客户端连接
        Socket socket = serverSocket.accept();
        // 客户端连接日志
        log.info("New client connected");
        // 为每个客户端创建一个新的线程
        new ServerThread(socket).start();
      }
    } catch (IOException e) {
      // 异常处理：打印堆栈跟踪
      e.printStackTrace();
    }
  }
}
