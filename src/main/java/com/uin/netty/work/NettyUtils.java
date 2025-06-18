package com.uin.netty.work;

import com.alibaba.fastjson.JSONObject;
import com.uin.netty.work.event.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyUtils {

  public static void sendEvent(Channel channel, String name, Event event) {
    if (channel != null) {
      String json = JSONObject.toJSONString(event);
      ByteBuf buf = Unpooled.buffer();
      buf.writeShort(json.getBytes().length);
      buf.writeBytes(json.getBytes());
      ChannelFuture future = channel.writeAndFlush(buf);
      future.addListener((ChannelFutureListener) f -> {
        if (!f.isSuccess()) {
          log.warn("write message({}) failed", channel.id());
        }
      });
    } else {
      log.warn("send event failed, channel not open: {}", name);
    }
  }
}
