//package com.uin.netty.protobuf;
//
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelPipeline;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.protobuf.ProtobufDecoder;
//import io.netty.handler.codec.protobuf.ProtobufEncoder;
//import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
//import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
//
//public class NettyClient {
//  public static void main(String[] args) throws InterruptedException {
//    EventLoopGroup group = new NioEventLoopGroup();
//    try {
//      Bootstrap bootstrap = new Bootstrap();
//      bootstrap.group(group)
//          .channel(NioSocketChannel.class)
//          .handler(new ChannelInitializer<SocketChannel>() {
//            @Override
//            protected void initChannel(SocketChannel ch) {
//              ChannelPipeline pipeline = ch.pipeline();
//              pipeline.addLast(new ProtobufVarint32FrameDecoder());
//              pipeline.addLast(new ProtobufDecoder(MessageProto.MyMessage.getDefaultInstance()));
//              pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
//              pipeline.addLast(new ProtobufEncoder());
//              pipeline.addLast(new MyClientHandler());
//            }
//          });
//      Channel channel = bootstrap.connect("localhost", 9000).sync().channel();
//
//      // 构造消息并发送
//      MessageProto.MyMessage msg = MessageProto.MyMessage.newBuilder()
//          .setId(1)
//          .setContent("Hello from client")
//          .build();
//      channel.writeAndFlush(msg);
//
//      channel.closeFuture().sync();
//    } finally {
//      group.shutdownGracefully();
//    }
//  }
//}
