//package com.uin.netty.protobuf;
//
//import io.netty.channel.ChannelHandlerContext;
//
//public class MyServerHandler extends SimpleChannelInboundHandler<MessageProto.MyMessage> {
//  @Override
//  protected void channelRead0(ChannelHandlerContext ctx, MessageProto.MyMessage msg) {
//    System.out.println("收到客户端消息: id=" + msg.getId() + ", content=" + msg.getContent());
//    // 构造响应消息
//    MessageProto.MyMessage response = MessageProto.MyMessage.newBuilder()
//        .setId(msg.getId() + 1000)
//        .setContent("服务端已收到: " + msg.getContent())
//        .build();
//    ctx.writeAndFlush(response);
//  }
//
//  @Override
//  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//    cause.printStackTrace();
//    ctx.close();
//  }
//}
