package com.uin.netty.reactor.multileThread;

import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.*;
import io.netty.channel.socket.nio.*;
import io.netty.handler.codec.string.*;
import io.netty.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class NettySingleReactorMultiWorkerServer {

  public static void main(String[] args) {
    // 单线程Boss，只处理连接接入
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    // 多线程worker 处理读写事件
    // 默认CPU核心数*2
    EventLoopGroup workerGroup = new NioEventLoopGroup(2);

    try {
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(bossGroup, workerGroup) // 设置boss + worker分组
          .channel(NioServerSocketChannel.class)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ChannelPipeline pipeline = ch.pipeline();
              // 编解码器
              pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
              pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));

              // 自定义业务逻辑
              pipeline.addLast(new SimpleServerHandler());
            }
          });

      ChannelFuture future = serverBootstrap.bind(8888).sync();
      log.info("server start success port:8888");
      future.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
