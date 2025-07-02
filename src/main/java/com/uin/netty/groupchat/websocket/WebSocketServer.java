package com.uin.netty.groupchat.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketServer {

  public static void main(String[] args) throws Exception {
    int port = 8080;
    EventLoopGroup boss = new NioEventLoopGroup();
    EventLoopGroup worker = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(boss, worker)
          .channel(NioServerSocketChannel.class)
          .childHandler(new WebSocketServerInitializer());

      Channel ch = b.bind(port).sync().channel();
      log.info("WebSocket服务器启动，端口：" + port);
      ch.closeFuture().sync();
    } finally {
      boss.shutdownGracefully();
      worker.shutdownGracefully();
    }
  }
}

