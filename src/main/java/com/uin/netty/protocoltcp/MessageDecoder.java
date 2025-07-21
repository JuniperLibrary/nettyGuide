package com.uin.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 * 自定义解码器
 */
public class MessageDecoder extends ByteToMessageDecoder {

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    // 检查长度
      if (in.readableBytes() < 8) {
          return;
      }

    in.markReaderIndex(); // 标记当前位置

    int magic = in.readInt();
    if (magic != 0x12345678) {
      ctx.close();
      return;
    }

    int length = in.readInt();
    if (in.readableBytes() < length) {
      in.resetReaderIndex(); // 不够一个完整包，等待下一次
      return;
    }

    byte[] content = new byte[length];
    in.readBytes(content);

    out.add(new MessageProtocol(content)); // 交给下一个 handler
  }
}
