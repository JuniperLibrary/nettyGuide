package com.uin.netty.proto;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MyClientHandler extends SimpleChannelInboundHandler<MessageProto.MyMessage> {
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, MessageProto.MyMessage msg) {
    System.out.println("收到服务端回复: id=" + msg.getId() + ", content=" + msg.getContent());
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
