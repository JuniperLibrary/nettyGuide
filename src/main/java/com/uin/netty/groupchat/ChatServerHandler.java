package com.uin.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

  private static final Map<String, Channel> userMap = new ConcurrentHashMap<>();
  private static final AttributeKey<String> USERNAME = AttributeKey.valueOf("username");

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String msg) {
    Channel channel = ctx.channel();

    if (msg.startsWith("login@")) {
      String username = msg.split("@", 2)[1];
      userMap.put(username, channel);
      channel.attr(USERNAME).set(username);
      channel.writeAndFlush("欢迎 " + username + " 加入聊天\n");
      return;
    }

    String sender = channel.attr(USERNAME).get();
    if (sender == null) {
      channel.writeAndFlush("请先登录，格式：login@用户名\n");
      return;
    }

    // 私聊
    if (msg.startsWith("@")) {
      String[] parts = msg.split(":", 2);
      String target = parts[0].substring(1);
      String content = parts.length > 1 ? parts[1] : "";

      Channel targetChannel = userMap.get(target);
      if (targetChannel != null) {
        targetChannel.writeAndFlush("[私聊] 来自 " + sender + ": " + content + "\n");
        channel.writeAndFlush("[你 -> " + target + "] " + content + "\n");
      } else {
        channel.writeAndFlush("用户 " + target + " 不在线\n");
      }
    } else {
      for (Channel ch : userMap.values()) {
        if (ch != channel) {
          ch.writeAndFlush("[群聊] " + sender + ": " + msg + "\n");
        }
      }
    }
  }

  @Override
  public void handlerRemoved(ChannelHandlerContext ctx) {
    Channel leaving = ctx.channel();
    String username = leaving.attr(USERNAME).get();
    if (username != null) {
      userMap.remove(username);
    }
  }
}

