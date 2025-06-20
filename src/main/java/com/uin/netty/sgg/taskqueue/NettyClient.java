package com.uin.netty.sgg.taskqueue;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class) // 使用 NIO Socket 通信
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });

            // 连接到服务器
            ChannelFuture future = bootstrap.connect("localhost", 8080).sync();

            // 发送消息
            String message = "Hello from Netty Client!";
            future.channel().writeAndFlush(Unpooled.copiedBuffer(message.getBytes()));

            // 等待关闭连接
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}

