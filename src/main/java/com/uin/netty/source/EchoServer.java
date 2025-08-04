package com.uin.netty.source;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLException;

public final class EchoServer {
  static final boolean SSL = System.getProperty("ssl") != null;
  static final int PORT = Integer.parseInt(System.getProperty("port","8007"));

  public static void main(String[] args) throws CertificateException, SSLException {
    final SslContext sslCtx;
    if (SSL){
      SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
      sslCtx = SslContextBuilder.forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey()).build();
    }else  {
      sslCtx = null;
    }
    // bossGroup 用于接收tcp请求，会将请求交给workerGroup
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG,100)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ChannelPipeline p = ch.pipeline();
              if (sslCtx != null) {
                p.addLast(sslCtx.newHandler(ch.alloc()));
              }
              //p.addLast(new LoggingHandler(LogLevel.INFO));
              p.addLast(new EchoServerHandler());
            }
          });
      // Start the server.
      ChannelFuture f = b.bind(PORT).sync();

      // Wait until the server socket is closed.
      f.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
