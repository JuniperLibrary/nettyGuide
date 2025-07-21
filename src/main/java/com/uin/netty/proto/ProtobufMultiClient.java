package com.uin.netty.proto;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProtobufMultiClient {

  public static void main(String[] args) throws Exception {
    Bootstrap bootstrap = new Bootstrap();
    EventLoopGroup group = new NioEventLoopGroup();

    bootstrap.group(group)
        .channel(NioSocketChannel.class)
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(
                new ProtobufVarint32FrameDecoder(),
                new ProtobufDecoder(MultiProto.ItemList.getDefaultInstance()),
                new ProtobufVarint32LengthFieldPrepender(),
                new ProtobufEncoder(),
                new ChannelInboundHandlerAdapter() {
                  @Override
                  public void channelActive(ChannelHandlerContext ctx) {
                    MultiProto.User user = MultiProto.User.newBuilder()
                        .setId(1).setName("Alice").build();
                    MultiProto.Product product = MultiProto.Product.newBuilder()
                        .setId(100).setName("Laptop").setPrice(999.99).build();

                    MultiProto.Item item1 = MultiProto.Item.newBuilder().setUser(user).build();
                    MultiProto.Item item2 = MultiProto.Item.newBuilder().setProduct(product)
                        .build();

                    MultiProto.ItemList itemList = MultiProto.ItemList.newBuilder()
                        .addItems(item1)
                        .addItems(item2)
                        .build();

                    ctx.writeAndFlush(itemList);
                  }
                }
            );
          }
        });

    bootstrap.connect("localhost", 8080).sync().channel().closeFuture().sync();
  }
}
