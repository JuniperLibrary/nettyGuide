package com.uin.netty.protocoltcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

public class NettyClient {

  public static void main(String[] args) throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();

    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(group)
        .channel(NioSocketChannel.class)
        .handler(new ChannelInitializer<SocketChannel>() {
          protected void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(new MessageDecoder());
            ch.pipeline().addLast(new MessageEncoder());
            ch.pipeline().addLast(new ClientHandler());
          }
        });

    ChannelFuture future = bootstrap.connect("127.0.0.1", 8080).sync();
    future.channel().closeFuture().sync();
  }
}

class ClientHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    for (int i = 0; i < 100; i++) {
      String msg = "Hello, Netty " + i;
      byte[] content = msg.getBytes(CharsetUtil.UTF_8);
      MessageProtocol protocol = new MessageProtocol(content);
      ctx.writeAndFlush(protocol);
    }
  }
}

