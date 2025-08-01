package com.uin.netty.groupchat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupChatClient {

  private final String host;
  private final int port;

  public GroupChatClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void run() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();

    try {

      Bootstrap bootstrap = new Bootstrap()
          .group(group)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {

              //得到pipeline
              ChannelPipeline pipeline = ch.pipeline();
              //加入相关handler
              pipeline.addLast("decoder", new StringDecoder());
              pipeline.addLast("encoder", new StringEncoder());
              //加入自定义的handler
              pipeline.addLast(new GroupChatClientHandler());
            }
          });

      ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
      //得到channel
      Channel channel = channelFuture.channel();
      log.info("-------  {} ------", channel.localAddress());
      //客户端需要输入信息，创建一个扫描器
      Scanner scanner = new Scanner(System.in);
      while (scanner.hasNextLine()) {
        String msg = scanner.nextLine();
        //通过channel 发送到服务器端
        channel.writeAndFlush(msg + "\r\n");
      }
    } finally {
      group.shutdownGracefully();
    }
  }

  public static void main(String[] args) throws Exception {
    new GroupChatClient("127.0.0.1", 8080).run();
  }
}
