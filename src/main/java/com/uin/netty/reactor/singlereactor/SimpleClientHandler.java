package com.uin.netty.reactor.singlereactor;

import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

  // 接收服务端的响应
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ByteBuf in = (ByteBuf) msg;
    log.info("Client received: {}",  in.toString(CharsetUtil.UTF_8));
  }

  // 异常处理
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}

