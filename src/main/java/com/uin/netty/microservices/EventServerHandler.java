package com.uin.netty.microservices;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventServerHandler extends SimpleChannelInboundHandler<String> {

  private static final Map<String, Channel> clients = new HashMap<>();

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    String clientId = ctx.channel().remoteAddress().toString();
    clients.put(clientId, ctx.channel());
    log.info("Client connected: " + clientId);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String msg) {
    log.info("Received message: {}", msg);
    // Broadcast the message to all clients
    for (Channel channel : clients.values()) {
      if (channel != ctx.channel()) {
        channel.writeAndFlush("Broadcast: " + msg + "\n");
      }
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    String clientId = ctx.channel().remoteAddress().toString();
    clients.remove(clientId);
    log.info("Client disconnected: {}", clientId);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
