package com.uin.netty.pubandsub;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PubSubServerHandler extends SimpleChannelInboundHandler<String> {

  @Getter
  private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    channels.add(ctx.channel());
    log.info("Client connected: {}", ctx.channel().remoteAddress());
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String msg) {
    // 在这里处理接收到的消息（如果有必要）
    log.info("Received message from client: {}", msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }

}


