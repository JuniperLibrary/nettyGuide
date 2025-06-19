package com.uin.netty.reactor.masterslavereactormultithrea.netty;

import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.*;
import io.netty.channel.socket.nio.*;
import io.netty.handler.codec.string.*;
import java.nio.charset.*;
import java.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class NettyClient {

  public static void main(String[] args) throws InterruptedException {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
      b.group(group)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
              ChannelPipeline p = ch.pipeline();
              p.addLast(new StringDecoder(StandardCharsets.UTF_8));
              p.addLast(new StringEncoder(StandardCharsets.UTF_8));
              p.addLast(new SimpleClientHandler());
            }
          });

      Channel ch = b.connect("localhost", 9090).sync().channel();
      log.info("Connected to Netty server. Type messages:");

      Scanner scanner = new Scanner(System.in);
      while (true) {
        String input = scanner.nextLine();
        if ("exit".equalsIgnoreCase(input)) {
          break;
        }
        ch.writeAndFlush(input + "\n");
      }
    } finally {
      group.shutdownGracefully();
    }
  }
}
