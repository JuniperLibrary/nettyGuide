package com.uin.netty.groupchat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatClient {

  public static void main(String[] args) throws Exception {
    String host = "localhost";
    int port = 8888;

    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap client = new Bootstrap();
      client.group(group)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) {
              ch.pipeline().addLast(new StringDecoder());
              ch.pipeline().addLast(new StringEncoder());
              ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                  System.out.println(msg.trim());
                }
              });
            }
          });

      Channel channel = client.connect(host, port).sync().channel();
      Scanner scanner = new Scanner(System.in);
      log.info("请输入 login@用户名 登录：");

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        channel.writeAndFlush(line);
      }
    } finally {
      group.shutdownGracefully();
    }
  }
}
