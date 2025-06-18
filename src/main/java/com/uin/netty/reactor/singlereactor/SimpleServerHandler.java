package com.uin.netty.reactor.singlereactor;

import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class SimpleServerHandler extends ChannelInboundHandlerAdapter {

  // 当接收到客户端消息时调用
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    // 将消息转换成字符串
    ByteBuf in = (ByteBuf) msg;
    String received = in.toString(CharsetUtil.UTF_8);
    log.info("Server received: {}", received);

    // 响应客户端
    ctx.writeAndFlush("Echo: " + received);
  }

  // 出现异常时关闭连接
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}

