package com.uin.netty.nativeio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 异步IO
 */
public class AsynchronousIOExample {

  /**
   * 程序的主入口点
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    try {
      // 打开异步文件通道以读取文件
      AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(Paths.get("input.txt"), StandardOpenOption.READ);

      // 分配一个缓冲区用于存储读取的数据
      ByteBuffer buffer = ByteBuffer.allocate(1024);

      // 异步读取文件数据
      fileChannel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
        /**
         * 读取操作完成时调用
         *
         * @param result 读取到的数据量，如果为-1表示到达文件末尾
         * @param attachment 用于存储数据的缓冲区
         */
        @Override
        public void completed(Integer result, ByteBuffer attachment) {
          if (result != -1) {
            // 数据读取成功，将缓冲区从写模式切换到读模式
            attachment.flip();
            // 处理读取的数据
          }
        }

        /**
         * 读取操作失败时调用
         *
         * @param exc 异常对象
         * @param attachment 用于存储数据的缓冲区
         */
        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
          // 处理失败情况
          exc.printStackTrace();
        }
      });

      // 主程序继续执行其他任务

    } catch (IOException e) {
      // 处理IO异常
      e.printStackTrace();
    }
  }

}
