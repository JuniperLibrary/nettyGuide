package com.uin.netty.reactor.multileThread;

import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.*;
import io.netty.channel.socket.nio.*;
import io.netty.handler.codec.string.*;
import io.netty.util.*;
import java.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class NettySingleReactorMultiWorkerClient {

  public static void main(String[] args) {
    NioEventLoopGroup group = new NioEventLoopGroup(1);

    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(group)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ChannelPipeline pipeline = ch.pipeline();
              pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
              pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
              pipeline.addLast(new SimpleClientHandler());
            }
          });

      Channel channel = bootstrap.connect("127.0.0.1", 8888).sync().channel();
      Scanner scanner = new Scanner(System.in);
      log.info("Client started. Type messages:");
      while (true) {
        String line = scanner.nextLine();
        if ("exit".equalsIgnoreCase(line)) {
          channel.close();
          break;
        }
        channel.writeAndFlush(line);
      }
    }catch (Exception e){

    }finally {
      group.shutdownGracefully();
    }
  }
}
