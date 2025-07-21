package com.uin.netty.protocoltcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

public class NettyServer {

  public static void main(String[] args) throws Exception {
    EventLoopGroup boss = new NioEventLoopGroup();
    EventLoopGroup worker = new NioEventLoopGroup();
    ServerBootstrap bootstrap = new ServerBootstrap();

    bootstrap.group(boss, worker)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          protected void initChannel(SocketChannel ch) {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new MessageDecoder());
            pipeline.addLast(new MessageEncoder());
            pipeline.addLast(new ServerHandler());
          }
        });

    bootstrap.bind(8080).sync();
  }
}

@Slf4j
class ServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) {
    log.info("收到内容: {}", new String(msg.getContent(), CharsetUtil.UTF_8));
  }
}

