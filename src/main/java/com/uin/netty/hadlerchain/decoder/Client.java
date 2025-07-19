package com.uin.netty.hadlerchain.decoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client {
    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                                    log.info("Client received: {}", msg.toString(CharsetUtil.UTF_8));
                                }
                            });
                        }
                    });

            Channel ch = b.connect("127.0.0.1", 8080).sync().channel();
            for (int i = 0; i < 3; i++) {
                String message = "Hello Netty " + i + "\n";
                ch.writeAndFlush(Unpooled.copiedBuffer(message.getBytes()));
                Thread.sleep(500); // 模拟间隔发送
            }

            ch.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
