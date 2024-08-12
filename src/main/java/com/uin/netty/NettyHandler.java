package com.uin.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyHandler extends ChannelInboundHandlerAdapter {

  private NettyMessageHandler handler;
  private Map<ChannelId, Channel> channels = new ConcurrentHashMap<>();

  public NettyHandler(NettyMessageHandler handler) {
    this.handler = handler;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    try {
      ByteBuf in = (ByteBuf) msg;
      byte[] body = new byte[in.readableBytes()];
      in.getBytes(0, body);
      this.handler.handleMessage(ctx.channel(), body);
    } finally {
      ReferenceCountUtil.release(msg);
    }
  }

  @Override
  public void channelActive(final ChannelHandlerContext ctx) throws Exception {
    log.info("netty connected({}) from {} to {}", ctx.channel().id(),
        ctx.channel().remoteAddress(), ctx.channel().localAddress());
    channels.put(ctx.channel().id(), ctx.channel());
    this.handler.handleChannelOpen(ctx.channel());
    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
    log.info("netty disconnected({}) from {} to {}", ctx.channel().id(),
        ctx.channel().remoteAddress(), ctx.channel().localAddress());
    channels.remove(ctx.channel().id());
    this.handler.handleChannelClose(ctx.channel());
    super.channelInactive(ctx);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.error("netty server handler caught exception", cause);
    ctx.close();
  }
}
