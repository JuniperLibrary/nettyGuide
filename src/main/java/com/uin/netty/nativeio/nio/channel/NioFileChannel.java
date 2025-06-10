package com.uin.netty.nativeio.nio.channel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NioFileChannel {

  public static void main(String[] args) {
    String str = "hello,尚硅谷";
    //创建一个输出流 -> channel
    FileOutputStream fileOutputStream = null;
    try {
      fileOutputStream = new FileOutputStream("file01.txt");
      //通过 fileOutputStream 获取对应的 FileChannel
      //这个 fileChannel 真实类型是 FileChannelImpl
      FileChannel fileChannel = fileOutputStream.getChannel();

      //创建一个缓冲区 ByteBuffer
      ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

      //将 str 放入 byteBuffer
      byteBuffer.put(str.getBytes());

      //对 byteBuffer 进行 flip
      byteBuffer.flip();

      //将 byteBuffer 数据写入到 fileChannel
      fileChannel.write(byteBuffer);
      fileOutputStream.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
