package com.uin.netty.sgg.http;

import lombok.extern.slf4j.Slf4j;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;

@Slf4j
public class NettyHttpClient {
    public static void main(String[] args) throws Exception {
        URI uri = new URI("http://localhost:8080/");
        String host = uri.getHost();

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpClientCodec());
                            p.addLast(new HttpObjectAggregator(65536));
                            p.addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) {
                                    log.info("响应内容: {}", response.content().toString(io.netty.util.CharsetUtil.UTF_8));
                                }
                            });
                        }
                    });

            Channel ch = b.connect(host, 8080).sync().channel();

            FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);

            ch.writeAndFlush(request).sync();
            ch.closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }
    }
}
