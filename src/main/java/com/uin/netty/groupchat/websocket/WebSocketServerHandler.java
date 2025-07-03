package com.uin.netty.groupchat.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

  private static final Map<String, Channel> userChannelMap = new ConcurrentHashMap<>();
  private static final AttributeKey<String> USERNAME = AttributeKey.valueOf("username");

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
    String msg = frame.text();
    if ("__ping__".equals(msg)) {
      // 可记录最近活跃时间或直接忽略
      return;
    }
    Channel channel = ctx.channel();

    if (msg.startsWith("login@")) {
      String username = msg.split("@", 2)[1];
      channel.attr(USERNAME).set(username);
      userChannelMap.put(username, channel);
      channel.writeAndFlush(new TextWebSocketFrame("登录成功，欢迎 " + username));
      broadcastUserList();
      return;
    }

    String sender = channel.attr(USERNAME).get();
    if (sender == null) {
      channel.writeAndFlush(new TextWebSocketFrame("请先登录，格式：login@用户名"));
      return;
    }

    if (msg.equals("list@")) {
      StringBuilder userList = new StringBuilder("当前在线用户：\n");
      for (String name : userChannelMap.keySet()) {
        userList.append("- ").append(name).append("\n");
      }
      channel.writeAndFlush(new TextWebSocketFrame(userList.toString()));
      return;
    }

    // 私聊
    if (msg.startsWith("@")) {
      String[] parts = msg.split(":", 2);
      String targetUser = parts[0].substring(1).trim(); // 去掉@，trim空格
      String content = parts.length > 1 ? parts[1] : "";

      Channel targetChannel = userChannelMap.get(targetUser);
      if (targetChannel != null) {
        targetChannel.writeAndFlush(
            new TextWebSocketFrame("[私聊] 来自 " + sender + ": " + content));
        if (targetChannel != channel) {  // 只有对方不是自己才发给自己私聊消息确认
          channel.writeAndFlush(new TextWebSocketFrame("[你 -> " + targetUser + "] " + content));
        }
      } else {
        channel.writeAndFlush(new TextWebSocketFrame("用户 " + targetUser + " 不在线"));
      }
      return;
    }

    // 群聊
    for (Map.Entry<String, Channel> entry : userChannelMap.entrySet()) {
      Channel ch = entry.getValue();
      if (ch == channel) {
        ch.writeAndFlush(new TextWebSocketFrame("[你]: " + msg));
      } else {
        ch.writeAndFlush(new TextWebSocketFrame("[群聊] " + sender + ": " + msg));
      }
    }

  }

  @Override
  public void handlerRemoved(ChannelHandlerContext ctx) {
    Channel ch = ctx.channel();
    String username = ch.attr(USERNAME).get();
    if (username != null) {
      userChannelMap.remove(username);
      broadcastUserList();
    }
  }

  private void broadcastUserList() {
    StringBuilder sb = new StringBuilder();
    sb.append("__userlist__"); // 前缀标识这是用户列表
    for (String user : userChannelMap.keySet()) {
      sb.append(user).append(",");
    }
    if (sb.charAt(sb.length() - 1) == ',') {
      sb.deleteCharAt(sb.length() - 1); // 去掉最后的逗号
    }

    for (Channel ch : userChannelMap.values()) {
      ch.writeAndFlush(new TextWebSocketFrame(sb.toString()));
    }
  }


}

