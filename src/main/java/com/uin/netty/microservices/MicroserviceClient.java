package com.uin.netty.microservices;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MicroserviceClient {

  private final String host;
  private final int port;

  public MicroserviceClient(String host, int port) {
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
              ch.pipeline().addLast(new MicroserviceHandler());
            }
          });

      Channel channel = b.connect(host, port).sync().channel();
      log.info("Microservice connected to server");

      // Start a new thread to listen for user input
      new Thread(() -> {
        Scanner scanner = new Scanner(System.in);
        while (true) {
          log.info("Enter event to publish: ");
          String event = scanner.nextLine();
          if ("exit".equalsIgnoreCase(event)) {
            channel.close();
            break;
          }
          channel.writeAndFlush(event + "\n");
        }
        scanner.close();
      }).start();

      channel.closeFuture().sync();
    } finally {
      group.shutdownGracefully();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    new MicroserviceClient("localhost", 8080).start();
  }
}

