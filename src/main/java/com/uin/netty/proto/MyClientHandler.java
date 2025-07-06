package com.uin.netty.proto;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyClientHandler extends SimpleChannelInboundHandler<MessageProto.MyMessage> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, MessageProto.MyMessage msg) {
    log.info("收到服务端回复: id={}, content={}", msg.getId(), msg.getContent());
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
