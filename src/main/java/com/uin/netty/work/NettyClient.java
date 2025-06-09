package com.uin.netty.work;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import java.net.ConnectException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClient {

  private final String host;
  private final int port;
  private final NettyMessageHandler handler;
  private boolean closed;

  public NettyClient(String host, int port, NettyMessageHandler handler) {
    this.host = host;
    this.port = port;
    this.handler = handler;
  }

  public void connect() {
    closed = false;
    ThreadFactory.startThread(String.format("netty-client"), () -> {
      while (!closed) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
          Bootstrap b = new Bootstrap();
          b.group(workerGroup);
          b.channel(NioSocketChannel.class);
          b.option(ChannelOption.SO_KEEPALIVE, true);
          b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000 * 10);
          b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
              ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 2, 0, 2));
              ch.pipeline().addLast(new NettyHandler(handler));
            }
          });

          // Start the client.
          ChannelFuture f = b.connect(host, port).sync();

          // Wait until the connection is closed.
          f.channel().closeFuture().sync();
        } catch (Exception e) {
          if (e.getCause() != null && e.getCause() instanceof ConnectException) {
            log.warn("connect failed: {}", e.getMessage());
          } else {
            log.error("netty client caught exception", e);
          }
        } finally {
          workerGroup.shutdownGracefully();
        }

        try {
          Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
          log.error("netty client caught exception when sleep", e);
        }
      }
    });
  }

  public void setClosed(boolean closed) {
    this.closed = closed;
  }
}
