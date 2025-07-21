package com.uin.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义编码器
 */
public class MessageEncoder extends MessageToByteEncoder<MessageProtocol> {

  @Override
  protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) {
    out.writeInt(msg.getMagic());
    out.writeInt(msg.getLength());
    out.writeBytes(msg.getContent());
  }
}

