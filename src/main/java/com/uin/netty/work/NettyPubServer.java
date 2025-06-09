package com.uin.netty.work;


import static com.uin.netty.work.NettyMessageHandler.MAX_FRAME_SIZE;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyPubServer {

  private final String host;
  private final int port;
  private final String name;
  private final NettyMessageHandler handler;
  private Channel channel;

  public NettyPubServer(String host, int port, String name, NettyMessageHandler handler) {
    this.host = host;
    this.port = port;
    this.name = name;
    this.handler = handler;
  }

  public void start() {
    ThreadFactory.startThread(String.format("netty-pub-%s", name), () -> {
      EventLoopGroup bossGroup = new NioEventLoopGroup();
      EventLoopGroup workerGroup = new NioEventLoopGroup();
      try {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
              @Override
              public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_SIZE, 0, 2, 0, 2));
                ch.pipeline().addLast(new NettyHandler(handler));
              }
            })
            .option(ChannelOption.SO_BACKLOG, 128)
            .option(ChannelOption.SO_REUSEADDR, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true);

        // Bind and start to accept incoming connections.
        ChannelFuture f = b.bind(host, port).sync();

        // Wait until the server socket is closed.
        // In this example, this does not happen, but you can do that to gracefully
        // shut down your server.
        channel = f.channel();
        channel.closeFuture().sync();
      } catch (InterruptedException e) {
        log.error("netty server caught exception", e);
      } finally {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
      }
    });
  }

  public void stop() {
    if (channel != null) {
      channel.close();
      channel = null;
    }
  }

  public boolean isStarted() {
    if (channel != null) {
      return channel.isOpen();
    }
    return false;
  }

}
