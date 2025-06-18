package com.uin.netty.reactor.singlereactor.multileThread;

import io.netty.channel.*;
import lombok.extern.slf4j.*;

@Slf4j
public class SimpleServerHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    String received = (String) msg;
    log.info("Server received: {}", received);
    ctx.writeAndFlush("Echo: " + received);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
