package com.uin.netty.proto;

import com.uin.netty.proto.UserProto.UserList;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
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
public class ProtobufServer {

  public static void main(String[] args) throws Exception {
    ServerBootstrap b = new ServerBootstrap();
    NioEventLoopGroup boss = new NioEventLoopGroup();
    NioEventLoopGroup worker = new NioEventLoopGroup();
    b.group(boss, worker)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(
                new ProtobufVarint32FrameDecoder(),
                new ProtobufDecoder(UserProto.UserList.getDefaultInstance()),
                new ProtobufVarint32LengthFieldPrepender(),
                new ProtobufEncoder(),
                new SimpleChannelInboundHandler<UserList>() {
                  @Override
                  protected void channelRead0(ChannelHandlerContext ctx, UserProto.UserList msg) {
                    log.info("Server received:");
                    msg.getUsersList().forEach(user ->
                        log.info("User:  id {} , name :{} ", user.getId(), user.getName()));
                  }
                }
            );
          }
        });
    b.bind(8080).sync();
    log.info("Server started on port 8080");
  }
}

