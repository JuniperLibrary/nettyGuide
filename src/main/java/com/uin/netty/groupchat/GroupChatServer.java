package com.uin.netty.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupChatServer {

  private int port;

  public GroupChatServer(int port) {
    this.port = port;
  }

  public void start() {
    //创建两个线程组
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup(); //8个NioEventLoop

    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .option(ChannelOption.SO_BACKLOG, 128)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
              //获取到pipeline
              ChannelPipeline pipeline = socketChannel.pipeline();
              //向pipeline加入解码器
              pipeline.addLast("decoder", new StringDecoder());
              //向pipeline加入编码器
              pipeline.addLast("encoder", new StringEncoder());
              //加入自己的业务处理handler
              pipeline.addLast(new GroupChatServerHandler());
            }
          });
      log.info("netty server start on port:{}", port);
      ChannelFuture channelFuture = bootstrap.bind(port).sync();
      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }

  public static void main(String[] args) {
    new GroupChatServer(8080).start();
  }
}
