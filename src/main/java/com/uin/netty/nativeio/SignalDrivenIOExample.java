package com.uin.netty.nativeio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 信号驱动IO
 */
public class SignalDrivenIOExample {

  /**
   * 主函数，用于异步读取文件数据
   * @param args 命令行参数，本例中未使用
   */
  public static void main(String[] args) {
    try {
      // 打开文件通道，异步读取指定文件
      AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(Paths.get("input.txt"), StandardOpenOption.READ);

      // 分配一个缓冲区，用于存储读取到的数据
      ByteBuffer buffer = ByteBuffer.allocate(1024);

      // 提交读取请求，从文件的开始位置读取数据到缓冲区
      Future<Integer> result = fileChannel.read(buffer, 0);

      // 等待读取操作完成，期间可以处理其他任务
      while (!result.isDone()) {
        // 可以在等待期间做其他事情
      }

      // 获取读取到的字节数
      int bytesRead = result.get();
      if (bytesRead != -1) {
        // 切换缓冲区模式，准备读取数据
        buffer.flip();
        // 处理读取的数据
      }

      // 关闭文件通道，释放资源
      fileChannel.close();

    } catch (IOException | InterruptedException | ExecutionException e) {
      // 处理可能发生的异常
      e.printStackTrace();
    }

  }

}
