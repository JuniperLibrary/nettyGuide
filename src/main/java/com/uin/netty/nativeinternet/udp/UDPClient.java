package com.uin.netty.nativeinternet.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

/**
 * UDP客户端类，用于演示如何通过UDP协议与服务器进行通信
 */
@Slf4j
public class UDPClient {

  /**
   * 主函数入口
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    // 创建DatagramSocket对象，用于发送和接收UDP数据报
    try (DatagramSocket socket = new DatagramSocket()) {
      // 获取服务器的地址
      InetAddress address = InetAddress.getByName("localhost");
      // 创建用于存储数据的字节数组
      byte[] buffer;

      // 创建Scanner对象，用于读取用户输入
      Scanner scanner = new Scanner(System.in);
      // 用于存储用户输入的文本
      String text;
      // 循环读取用户输入，直到用户输入"bye"退出
      do {
        // 提示用户输入文本
        log.info("Enter text: ");
        text = scanner.nextLine();
        // 将用户输入的文本转换为字节数组
        buffer = text.getBytes();

        // 创建用于发送的数据报对象
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 9876);
        // 发送数据报到服务器
        socket.send(packet);

        // 创建用于接收的缓冲数据报对象
        packet = new DatagramPacket(new byte[1024], 1024);
        // 接收服务器的响应数据报
        socket.receive(packet);
        // 将接收到的数据报转换为字符串形式
        String response = new String(packet.getData(), 0, packet.getLength());
        // 记录服务器的响应
        log.info("Server replied: {}", response);
      } while (!text.equals("bye"));
    } catch (IOException e) {
      // 处理可能发生的IO异常
      e.printStackTrace();
    }
  }
}
