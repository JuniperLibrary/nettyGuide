package com.uin.netty.proto;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class ProtoBufClient {

  public static void main(String[] args) throws Exception {
    Bootstrap b = new Bootstrap();
    NioEventLoopGroup group = new NioEventLoopGroup();
    b.group(group)
        .channel(NioSocketChannel.class)
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(
                new ProtobufVarint32FrameDecoder(),
                new ProtobufDecoder(UserProto.UserList.getDefaultInstance()),
                new ProtobufVarint32LengthFieldPrepender(),
                new ProtobufEncoder(),
                new ChannelInboundHandlerAdapter() {
                  @Override
                  public void channelActive(ChannelHandlerContext ctx) {
                    UserProto.User user1 = UserProto.User.newBuilder()
                        .setId(1).setName("Alice").build();
                    UserProto.User user2 = UserProto.User.newBuilder()
                        .setId(2).setName("Bob").build();

                    UserProto.UserList userList = UserProto.UserList.newBuilder()
                        .addUsers(user1)
                        .addUsers(user2)
                        .build();

                    ctx.writeAndFlush(userList);
                  }
                }
            );
          }
        });
    b.connect("localhost", 8080).sync().channel().closeFuture().sync();
  }
}

