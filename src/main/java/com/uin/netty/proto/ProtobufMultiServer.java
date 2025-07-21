package com.uin.netty.proto;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProtobufMultiServer {

  public static void main(String[] args) throws Exception {
    ServerBootstrap bootstrap = new ServerBootstrap();
    EventLoopGroup boss = new NioEventLoopGroup();
    EventLoopGroup worker = new NioEventLoopGroup();

    bootstrap.group(boss, worker)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(
                new ProtobufVarint32FrameDecoder(),
                new ProtobufDecoder(MultiProto.ItemList.getDefaultInstance()),
                new ProtobufVarint32LengthFieldPrepender(),
                new ProtobufEncoder(),
                new SimpleChannelInboundHandler<MultiProto.ItemList>() {
                  @Override
                  protected void channelRead0(ChannelHandlerContext ctx, MultiProto.ItemList msg) {
                    for (MultiProto.Item item : msg.getItemsList()) {
                      if (item.hasUser()) {
                        log.info("User: {}", item.getUser().getName());
                      } else if (item.hasProduct()) {
                        log.info("Product: {} , Price : {}", item.getProduct().getName(),
                            item.getProduct().getPrice());
                      }
                    }
                  }
                }
            );
          }
        });

    bootstrap.bind(8080).sync();
    log.info("Server running on port 8080");
  }
}
