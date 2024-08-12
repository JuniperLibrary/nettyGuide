package com.uin.netty.nativeinternet.url;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;

/**
 * HttpPostExample 类演示了如何使用 Java 的 HttpURLConnection 进行 HTTP POST 请求
 */
@Slf4j
public class HttpPostExample {

  /**
   * 主方法执行 HTTP POST 请求
   *
   * @param args 命令行参数，本例中未使用
   */
  public static void main(String[] args) {
    // 目标 URL，是一个提供公共 REST API 的服务
    String url = "https://jsonplaceholder.typicode.com/posts";
    // 待发送的 JSON 格式的数据
    String jsonInputString = "{\"title\":\"foo\",\"body\":\"bar\",\"userId\":1}";

    try {
      // 建立与目标 URL 的连接
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      // 设置请求方法为 POST
      connection.setRequestMethod("POST");
      // 设置请求体的类型为应用/json，指定字符集为 utf-8
      connection.setRequestProperty("Content-Type", "application/json; utf-8");
      // 设置接受的数据类型为应用/json
      connection.setRequestProperty("Accept", "application/json");
      // 表示连接会输出内容
      connection.setDoOutput(true);

      // 获取连接的输出流，用于向服务器发送数据
      try (OutputStream os = connection.getOutputStream()) {
        // 将 JSON 字符串转换为字节数组，准备发送
        byte[] input = jsonInputString.getBytes("utf-8");
        // 向输出流写入数据
        os.write(input, 0, input.length);
      }

      // 获取服务器的响应码
      int responseCode = connection.getResponseCode();
      // 记录响应码
      log.info("Response Code: {}", responseCode);

      // 读取服务器的响应
      try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        String line;
        // 用于存储响应内容
        StringBuffer response = new StringBuffer();
        while ((line = in.readLine()) != null) {
          response.append(line);
        }
        // 记录响应内容
        log.info("Response: {}", response.toString());
      }
    } catch (IOException e) {
      // 处理 IO 异常
      e.printStackTrace();
    }
  }
}
