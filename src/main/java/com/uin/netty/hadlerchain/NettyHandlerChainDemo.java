package com.uin.netty.hadlerchain;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyHandlerChainDemo {
    public static void main(String[] args) {
        try {
            NioEventLoopGroup boss = new NioEventLoopGroup(1);
            NioEventLoopGroup worker = new NioEventLoopGroup();

            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    // 按照顺序添加handler
                    pipeline.addLast(new MyInboundHandler1());
                    pipeline.addLast(new MyInboundHandler2());
                    // 出站
                    pipeline.addLast(new MyOutboundHandler());
                }
            });

            b.bind(8080).sync();
            log.info("Netty server started on port 8080");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    // 入站处理器1
    static class MyInboundHandler1 extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            log.info("MyInboundHandler1.channelRead");

            // 继续传播入站事件
            ctx.fireChannelRead(msg);
        }
    }

    // 入站处理器2
    static class MyInboundHandler2 extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            log.info("MyInboundHandler2.channelRead");

            // 响应客户端（出站事件）
            ctx.writeAndFlush(Unpooled.copiedBuffer("Hello Client", CharsetUtil.UTF_8));
        }
    }

    // 出站处理器
    static class MyOutboundHandler extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
            log.info("MyOutboundHandler.write");

            // 继续传播出站事件
            ctx.write(msg, promise);
        }
    }
}
