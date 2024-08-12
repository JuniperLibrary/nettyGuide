package com.uin.netty.nativeinternet.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UDPServer {

  /**
   * 主函数，启动UDP服务器并监听指定端口
   * @param args 命令行参数，本例中未使用
   */
  public static void main(String[] args) {
    // 创建并打开一个UDP端口，开始监听9876端口
    try (DatagramSocket socket = new DatagramSocket(9876)) {
      // 接收数据的缓冲区
      byte[] buffer = new byte[1024];
      // 日志记录：UDP服务器正在监听端口9876
      log.info("UDP server is listening on port 9876");

      // 服务器持续监听和响应数据包
      while (true) {
        // 创建一个数据报用于接收数据
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
        // 接收数据报
        socket.receive(datagramPacket);
        // 解析接收到的数据
        String received = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
        // 日志记录：接收到来自客户端的数据
        log.info("Received: {}", received);

        // 构造响应数据
        String response = "Echo: " + received;
        // 将响应数据转换为字节数组
        byte[] responseData = response.getBytes();
        // 创建一个数据报用于发送响应数据
        DatagramPacket responsePacket = new DatagramPacket(
            responseData, responseData.length, datagramPacket.getAddress(), datagramPacket.getPort()
        );
        // 发送响应数据报
        socket.send(responsePacket);
      }
    } catch (IOException e) {
      // 异常处理：转换为运行时异常并抛出
      throw new RuntimeException(e);
    }
  }
}
