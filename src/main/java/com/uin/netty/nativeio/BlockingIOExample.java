package com.uin.netty.nativeio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * BIO
 */
@Slf4j
public class BlockingIOExample {

  /**
   * 程序的入口点 主要功能是将输入文件复制到输出文件 该方法使用了try-with-resources语句来自动管理文件输入输出流
   *
   * @param args 命令行参数
   * @throws FileNotFoundException 如果输入文件不存在或无法访问
   */
  public static void main(String[] args) throws FileNotFoundException {
    // 使用try-with-resources语句确保文件流在使用后能自动关闭
    try (FileInputStream fis = new FileInputStream("input.txt");
        FileOutputStream fos = new FileOutputStream("output.txt")) {

      // 创建一个1KB的字节数组用于读取文件内容
      byte[] buffer = new byte[1024];
      // 用于存储每次读取的字节数
      int bytesRead;

      // 循环读取文件内容直到文件末尾
      while ((bytesRead = fis.read(buffer)) != -1) {
        // 将读取的内容写入到输出文件
        fos.write(buffer, 0, bytesRead);
      }

    } catch (IOException e) {
      // 异常处理：打印出异常信息
      e.printStackTrace();
    }
  }
}
