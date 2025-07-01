package com.uin.netty.unpooled;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnpooledExample {

  public static void main(String[] args) {
    // 1. 使用 copiedBuffer（拷贝数据）
    ByteBuf buf1 = Unpooled.copiedBuffer("Hello Netty", StandardCharsets.UTF_8);
    log.info(buf1.toString(StandardCharsets.UTF_8));

    // 2. 使用 wrappedBuffer（不拷贝数据）
    byte[] data = "Wrapped Data".getBytes(StandardCharsets.UTF_8);
    ByteBuf buf2 = Unpooled.wrappedBuffer(data);
    log.info(buf2.toString(StandardCharsets.UTF_8));

    // 修改原数组，ByteBuf 内容也会变化
    data[0] = 'X';
    log.info(buf2.toString(StandardCharsets.UTF_8)); // Xrapped Data

    // 3. 创建一个新 buffer 并写入内容
    ByteBuf buf3 = Unpooled.buffer(10); // 初始容量10
    buf3.writeBytes(new byte[]{1, 2, 3});
    log.info(String.valueOf(buf3.readByte())); // 1
  }
}
