package com.uin.netty.reactor.singlereactor;

import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.group.*;
import io.netty.util.*;
import io.netty.util.concurrent.*;
import lombok.extern.slf4j.*;

@Slf4j
public class SimpleServerHandler extends ChannelInboundHandlerAdapter {

  // 用于保存所有连接的客户端Channel（线程安全）
  private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


  /**
   * 有新连接时触发：添加进 ChannelGroup
   *
   * @param ctx
   * @throws Exception
   */
  @Override
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    Channel channelled = ctx.channel();
    channels.add(channelled);
    log.info("Client joined : {}", channelled.remoteAddress());
  }

  // 当接收到客户端消息时调用
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    Channel sender = ctx.channel();
    // 将消息转换成字符串
    ByteBuf in = (ByteBuf) msg;
    String received = in.toString(CharsetUtil.UTF_8);
    log.info("Received from :{} , {}", sender.remoteAddress(), received);
//    log.info("Server received: {}", received);

    // 向所有连接的客户端广播该消息
    for (Channel channel : channels) {
      if (channel != sender) {
        channel.writeAndFlush(Unpooled.copiedBuffer("[From " + sender.remoteAddress() + "]: " + received, CharsetUtil.UTF_8));
      }

//      else {
//        channel.writeAndFlush(Unpooled.copiedBuffer("[You]: " + received, CharsetUtil.UTF_8));
//      }
    }

    // 响应客户端
//    ctx.writeAndFlush("Echo: " + received);
  }

  // 客户端断开连接时移除
  @Override
  public void handlerRemoved(ChannelHandlerContext ctx) {
    Channel leaving = ctx.channel();
    log.info("Client left: {}", leaving.remoteAddress());
    channels.remove(leaving);
  }

  // 出现异常时关闭连接
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}

