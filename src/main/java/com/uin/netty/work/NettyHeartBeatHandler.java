package com.uin.netty.work;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyHeartBeatHandler extends ChannelInboundHandlerAdapter {

  private static final int HEARTBEAT_INTERVAL = 30;

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    scheduleSendHeartBeat(ctx);
    super.channelActive(ctx);
  }

  private void scheduleSendHeartBeat(ChannelHandlerContext ctx) {
    ctx.executor().schedule(() -> {
      if (ctx.channel().isActive()) {
        Event event = EventBuilder.buildEvent(EventTypeManager.HEARTBEAT, "netty-client", 1);
        if (log.isDebugEnabled()) {
          log.debug("netty client send heartbeat to server. {}", event);
        }
        NettyUtils.sendEvent(ctx.channel(), "heartbeat", event);
        scheduleSendHeartBeat(ctx);
      }
    }, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
  }
}
