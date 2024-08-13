package com.uin.netty.pubandsub;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PublisherServer {

  private final int port;

  public PublisherServer(int port) {
    this.port = port;
  }

  public void start() throws InterruptedException {
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .option(ChannelOption.SO_BACKLOG, 100)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
              ch.pipeline().addLast(new StringDecoder());
              ch.pipeline().addLast(new StringEncoder());
              ch.pipeline().addLast(new PubSubServerHandler());
            }
          });

      ChannelFuture f = b.bind(port).sync();
      log.info("Publisher started and listening on {}", f.channel().localAddress());

      // 启动一个线程监听用户输入
      new Thread(() -> {
        Scanner scanner = new Scanner(System.in);
        while (true) {
          log.info("Enter message to broadcast: ");
          String message = scanner.nextLine();
          if ("exit".equalsIgnoreCase(message)) {
            log.info("Shutting down server...");
            f.channel().close();
            break;
          }
          // 将用户输入的消息广播给所有客户端
          for (Channel channel : PubSubServerHandler.getChannels()) {
            channel.writeAndFlush(message + "\n");
          }
        }
        scanner.close();
      }).start();

      f.channel().closeFuture().sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    new PublisherServer(8080).start();
  }
}
