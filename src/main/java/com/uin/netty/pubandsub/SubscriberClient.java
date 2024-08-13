package com.uin.netty.pubandsub;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubscriberClient {

  private final String host;
  private final int port;

  public SubscriberClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void start() throws InterruptedException {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
      b.group(group)
          .channel(NioSocketChannel.class)
          .option(ChannelOption.SO_KEEPALIVE, true)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
              ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
              ch.pipeline().addLast(new StringDecoder());
              ch.pipeline().addLast(new StringEncoder());
              ch.pipeline().addLast(new SubscriberHandler());
            }
          });

      b.connect(host, port).sync().channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    new SubscriberClient("localhost", 8080).start();
  }
}

