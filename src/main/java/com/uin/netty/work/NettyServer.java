package com.uin.netty.work;

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

/**
 * Netty服务器实现，用于处理特定端口和名称的网络连接
 * <p>
 * 该服务器使用netty框架来处理网络通信，并通过指定的处理器处理消息
 */
@Slf4j
public class NettyServer {

  // 服务器监听的端口号
  private final int port;
  // 服务器的名称，用于标识和日志记录
  private final String name;
  // 处理网络消息的处理器
  private final NettyMessageHandler handler;
  // 服务器的通道，用于处理网络连接
  private Channel channel;

  /**
   * 构造函数，初始化Netty服务器。
   *
   * @param port    服务器监听的端口号
   * @param name    服务器的名称，用于标识和日志记录
   * @param handler 处理网络消息的处理器
   */
  public NettyServer(int port, String name, NettyMessageHandler handler) {
    this.port = port;
    this.name = name;
    this.handler = handler;
  }

  /**
   * 启动服务器，绑定到指定的端口，并开始监听传入的连接。
   */
  public void start() {
    // 使用自定义的线程工厂启动一个后台线程来执行服务器的启动操
    ThreadFactory.startThread(String.format("netty-server-%s", name), () -> {
      // 创建两个EventLoopGroup 一个是boss专门用来接受连接 可以理解为accept事件
      // 另一个是worker，可以理解为除了accept之外的的其他事件，处理子任务
      // boss线程一般设置一个线程，设置多个也只会用到一个，而且多个目前没有应用场景，
      // worker线程通常要根据服务器调优，如果不写默认就是cpu的两倍。
      EventLoopGroup bossGroup = new NioEventLoopGroup();
      EventLoopGroup workerGroup = new NioEventLoopGroup();
      try {
        // 创建服务器引导对象
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 配置服务器的参数和处理器
        bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
              @Override
              public void initChannel(SocketChannel ch) {
                // 添加帧解码器，用于处理固定长度的帧
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 2, 0, 2));
                // 添加自定义处理器，用于处理解码后的消息
                ch.pipeline().addLast(new NettyHandler(handler));
              }
            })
            .option(ChannelOption.SO_BACKLOG, 128)
            // 启用保持连接
            .childOption(ChannelOption.SO_KEEPALIVE, true);

        // Bind and start to accept incoming connections.
        // 绑定端口，开始接受传入的连接
        ChannelFuture f = bootstrap.bind(port).sync();

        // Wait until the server socket is closed.
        // In this example, this does not happen, but you can do that to gracefully
        // shut down your server.
        // 等待服务器套接字通道关闭
        channel = f.channel();
        channel.closeFuture().sync();
      } catch (InterruptedException e) {
        log.error("netty server caught exception", e);
      } finally {
        // 关闭boss线程 和 worker线程
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

}
