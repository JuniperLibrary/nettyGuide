package com.uin.netty.reactor.masterslavereactormultithrea.netty;

import io.netty.channel.*;
import lombok.extern.slf4j.*;

@Slf4j
public class SimpleServerHandler extends SimpleChannelInboundHandler<String> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String msg) {
    log.info("Server received: {}", msg);
    ctx.writeAndFlush("Echo: " + msg + "\n");
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
