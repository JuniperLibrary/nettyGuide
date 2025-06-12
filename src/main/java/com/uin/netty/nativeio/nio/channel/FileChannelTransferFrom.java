package com.uin.netty.nativeio.nio.channel;

import java.io.*;
import java.nio.channels.*;

public class FileChannelTransferFrom {

  public static void main(String[] args) {
    try(FileInputStream fileInputStream = new FileInputStream("a.png");
        FileChannel fileChannel = fileInputStream.getChannel();
        FileOutputStream fileOutputStream = new FileOutputStream("b.png");
        FileChannel fileChannel2 = fileOutputStream.getChannel()){

      //使用 transferForm 完成拷贝
      fileChannel2.transferFrom(fileChannel, 0, fileChannel.size());

      // try resource 会自动关闭
//      fileChannel.close();
//      fileChannel2.close();
//      fileInputStream.close();
//      fileOutputStream.close();
    }catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
