package com.uin.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {
//public static List<Channel> channels = new ArrayList<Channel>();

  //使用一个hashmap 管理
  //public static Map<String, Channel> channels = new HashMap<String,Channel>();

  //定义一个channle 组，管理所有的channel
  //GlobalEventExecutor.INSTANCE) 是全局的事件执行器，是一个单例
  private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  //handlerAdded 表示连接建立，一旦连接，第一个被执行
  //将当前channel 加入到  channelGroup
  @Override
  public void handlerAdded(ChannelHandlerContext ctx) {
    Channel channel = ctx.channel();
    //将该客户加入聊天的信息推送给其它在线的客户端

    //该方法会将 channelGroup 中所有的channel 遍历，并发送 消息，
    //我们不需要自己遍历
    channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 加入聊天" + sdf.format(new java.util.Date()) + " \n");
    channelGroup.add(channel);


  }

  //断开连接, 将xx客户离开信息推送给当前在线的客户
  @Override
  public void handlerRemoved(ChannelHandlerContext ctx) {

    Channel channel = ctx.channel();
    channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 离开了\n");
    log.info("channelGroup size :{}", channelGroup.size());

  }

  //表示channel 处于活动状态, 提示 xx上线
  @Override
  public void channelActive(ChannelHandlerContext ctx) {

    log.info(" {} 上线了~", ctx.channel().remoteAddress());
  }

  //表示channel 处于不活动状态, 提示 xx离线了
  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    log.info("{} 离线了~", ctx.channel().remoteAddress());
  }

  //读取数据
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

    //获取到当前channel
    Channel channel = ctx.channel();
    //这时我们遍历channelGroup, 根据不同的情况，回送不同的消息

    channelGroup.forEach(ch -> {
      if (channel != ch) { //不是当前的channel,转发消息
        ch.writeAndFlush("[客户]" + channel.remoteAddress() + " 发送了消息" + msg + "\n");
      } else {//回显自己发送的消息给自己
        ch.writeAndFlush("[自己]发送了消息" + msg + "\n");
      }
    });
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    //关闭通道
    ctx.close();
  }
}
