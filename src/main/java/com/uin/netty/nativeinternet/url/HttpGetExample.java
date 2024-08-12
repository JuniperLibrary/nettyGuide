package com.uin.netty.nativeinternet.url;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;

/**
 * HttpGetExample 类演示了如何使用 Java 的 HttpURLConnection 类实现 HTTP GET 请求
 */
@Slf4j
public class HttpGetExample {

  /**
   * 主函数执行 HTTP GET 请求并打印响应
   *
   * @param args 命令行参数，本例中未使用
   */
  public static void main(String[] args) {
    // 定义请求的 URL 地址
    String url = "https://jsonplaceholder.typicode.com/posts/1";

    try {
      // 创建 HttpURLConnection 对象并建立与 URL 的连接
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      // 设置请求方法为 GET
      connection.setRequestMethod("GET");

      // 获取服务器响应码
      int responseCode = connection.getResponseCode();
      // 日志记录响应码
      log.info("Response Code: {}", responseCode);

      // 使用 BufferedReader 读取服务器响应
      try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        String line;
        // 创建 StringBuffer 对象保存响应内容
        StringBuffer response = new StringBuffer();
        // 循环读取响应内容
        while ((line = in.readLine()) != null) {
          response.append(line);
        }
        // 日志记录完整的响应内容
        log.info("Response: {}", response.toString());
      }
    } catch (IOException e) {
      // 打印异常信息
      e.printStackTrace();
    }
  }
}
