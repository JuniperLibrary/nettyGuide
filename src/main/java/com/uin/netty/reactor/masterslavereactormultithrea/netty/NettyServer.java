package com.uin.netty.reactor.masterslavereactormultithrea.netty;

import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.*;
import io.netty.channel.socket.nio.*;
import io.netty.handler.codec.string.*;
import java.nio.charset.*;
import lombok.extern.slf4j.*;

@Slf4j
public class NettyServer {

  private final int port;

  public NettyServer(int port) {
    this.port = port;
  }

  public void run() {
    NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
              ChannelPipeline p = ch.pipeline();
              p.addLast(new StringDecoder(StandardCharsets.UTF_8));
              p.addLast(new StringEncoder(StandardCharsets.UTF_8));
              p.addLast(new SimpleServerHandler());
            }
          });
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }

  public static void main(String[] args) {
    new NettyServer(9090).run();
  }
}
