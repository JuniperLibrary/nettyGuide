package com.uin.netty.groupchat.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;

public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
  @Override
  protected void initChannel(SocketChannel ch) {
    ChannelPipeline pipeline = ch.pipeline();

    // HTTP协议支持
    pipeline.addLast(new HttpServerCodec());
    pipeline.addLast(new HttpObjectAggregator(65536));
    pipeline.addLast(new ChunkedWriteHandler());

    // 心跳检测：10s未收到客户端消息，出发读空闲
    pipeline.addLast(new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS));
    pipeline.addLast(new HeartbeatHandler()); // 自定义心跳处理器

    // WebSocket协议支持
    pipeline.addLast(new WebSocketServerProtocolHandler("/chat"));

    // 业务处理
    pipeline.addLast(new WebSocketServerHandler());
  }
}

