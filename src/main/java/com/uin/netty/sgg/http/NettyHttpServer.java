package com.uin.netty.sgg.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyHttpServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpServerCodec());
                            p.addLast(new HttpObjectAggregator(65536)); // 聚合HttpMessage为FullHttpRequest
                            p.addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
                                    String content = "Hello from Netty HTTP Server!";
                                    FullHttpResponse response = new DefaultFullHttpResponse(
                                            req.protocolVersion(), HttpResponseStatus.OK,
                                            ctx.alloc().buffer().writeBytes(content.getBytes()));

                                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                                    response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, content.length());

                                    ctx.writeAndFlush(response);
                                }
                            });
                        }
                    });

            ChannelFuture future = bootstrap.bind(8080).sync();
            log.info("HTTP 服务器已启动，端口: 8080");
            future.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
