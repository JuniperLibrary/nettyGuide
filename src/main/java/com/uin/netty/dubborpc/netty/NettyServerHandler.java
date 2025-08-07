package com.uin.netty.dubborpc.netty;

import com.uin.netty.dubborpc.customer.ClientBootstrap;
import com.uin.netty.dubborpc.provider.HelloServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务器这边handler比较简单
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    //获取客户端发送的消息，并调用服务
    log.info("msg= {}", msg);
    //客户端在调用服务器的api 时，我们需要定义一个协议
    //比如我们要求 每次发消息是都必须以某个字符串开头 "HelloService#hello#你好"
    if (msg.toString().startsWith(ClientBootstrap.providerName)) {
      String result = new HelloServiceImpl().hello(msg.toString().substring(msg.toString().lastIndexOf("#") + 1));
      ctx.writeAndFlush(result);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.close();
  }

  public static void main(String[] args) {
    String s = "HelloService#hello#";

    System.out.println(s.substring(18));
    System.out.println(s.substring(s.lastIndexOf("#") + 1));
  }
}
