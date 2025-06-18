package com.uin.netty.reactor.singlereactor;

import io.netty.bootstrap.*;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.*;
import io.netty.channel.socket.nio.*;
import io.netty.util.*;
import java.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class NettySingleReactorClient {

  public static void main(String[] args) throws InterruptedException {
    // 创建客户端事件循环组（单线程）
    EventLoopGroup group = new NioEventLoopGroup(1);

    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(group) // 指定事件循环组
          .channel(NioSocketChannel.class) // 使用NIO通道
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
              // 添加自定义客户端处理器
              ch.pipeline().addLast(new SimpleClientHandler());
            }
          });

      // 连接到服务器
      ChannelFuture future = bootstrap.connect("localhost", 8888).sync();

      // 发送消息的逻辑
      Scanner scanner = new Scanner(System.in);
      Channel channel = future.channel();
      log.info("Enter message (type 'exit' to quit):");

      while (true) {
        String input = scanner.nextLine();
        if ("exit".equalsIgnoreCase(input)) {
          channel.close();
          break;
        }

        // 向服务端发送消息
        channel.writeAndFlush(Unpooled.copiedBuffer(input, CharsetUtil.UTF_8));
      }

      // 等待连接关闭
      channel.closeFuture().sync();
    } finally {
      group.shutdownGracefully(); // 释放资源
    }
  }
}
