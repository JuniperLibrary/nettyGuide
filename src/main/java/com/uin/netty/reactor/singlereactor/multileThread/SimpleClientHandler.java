package com.uin.netty.reactor.singlereactor.multileThread;

import io.netty.channel.*;
import lombok.extern.slf4j.*;

@Slf4j
public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    log.info("Client received: {}", msg);
  }
}

