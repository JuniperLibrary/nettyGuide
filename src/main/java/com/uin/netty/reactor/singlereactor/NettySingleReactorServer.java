package com.uin.netty.reactor.singlereactor;


import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.*;
import io.netty.channel.socket.nio.*;
import lombok.extern.slf4j.*;

@Slf4j
public class NettySingleReactorServer {

  public static void main(String[] args) {
    // 创建一个线程的事件循环组（即单Reactor + 单线程）
    EventLoopGroup group = new NioEventLoopGroup(1);

    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap.group(group) // 使用同一个事件循环组
          .channel(NioServerSocketChannel.class) // 使用NIO通道
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
              // 添加自定义处理器
              ch.pipeline().addLast(new SimpleServerHandler());
            }
          });

      // 绑定端口并启动
      ChannelFuture future = bootstrap.bind(8888).sync();
      log.info("Server started on port 8888");

      // 等待关闭
      future.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      // 释放资源
      group.shutdownGracefully();
    }
  }
}
