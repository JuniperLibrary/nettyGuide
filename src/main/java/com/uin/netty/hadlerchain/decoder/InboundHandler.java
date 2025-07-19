package com.uin.netty.hadlerchain.decoder;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("InboundHandler received: " + msg);
        // 往客户端回写消息，触发出站
        ctx.writeAndFlush(Unpooled.copiedBuffer(("Server received: " + msg + "\n").getBytes()));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Client connected: {}" , ctx.channel().remoteAddress());
    }
}

