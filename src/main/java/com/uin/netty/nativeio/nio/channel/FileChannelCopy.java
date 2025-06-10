package com.uin.netty.nativeio.nio.channel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelCopy {

  public static void main(String[] args) {
    try (
        FileInputStream fileInputStream = new FileInputStream("file01.txt");
        FileChannel fileInputStreamChannel = fileInputStream.getChannel();
        FileOutputStream fileOutputStream = new FileOutputStream("file02.txt");
        FileChannel fileOutputStreamChannel = fileOutputStream.getChannel()
    ) {
      ByteBuffer byteBufferInput = ByteBuffer.allocate(1024);

      while (true) {
        byteBufferInput.clear();
        int read = fileInputStreamChannel.read(byteBufferInput);
        System.out.println("read :" + read);

        if (read == -1) {
          break;
        }

        byteBufferInput.flip();
        fileOutputStreamChannel.write(byteBufferInput);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
