package com.uin.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty处理器类，用于处理服务器接收到的各种事件和消息
 */
@Slf4j
public class NettyHandler extends ChannelInboundHandlerAdapter {

  // 消息处理逻辑的处理器
  private NettyMessageHandler handler;
  // 用于存储所有通道的映射
  private Map<ChannelId, Channel> channels = new ConcurrentHashMap<>();

  /**
   * 构造函数，初始化NettyMessageHandler
   *
   * @param handler 消息处理逻辑的处理器
   */
  public NettyHandler(NettyMessageHandler handler) {
    this.handler = handler;
  }

  /**
   * 当通道读取操作完成时被调用
   *
   * @param ctx 上下文对象，包含通道及其相关的辅助信息
   * @param msg 从通道读取的消息
   */
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    try {
      // 将读取到的消息转换为ByteBuf
      ByteBuf in = (ByteBuf) msg;
      // 创建一个用于存储消息内容的字节数组
      byte[] body = new byte[in.readableBytes()];
      // 将ByteBuf中的数据读取到字节数组中
      in.getBytes(0, body);
      // 调用消息处理器处理消息
      this.handler.handleMessage(ctx.channel(), body);
    } finally {
      // 释放消息，避免内存泄漏
      ReferenceCountUtil.release(msg);
    }
  }

  /**
   * 当通道变得活跃时（例如，连接建立后）被调用
   *
   * @param ctx 上下文对象，包含通道及其相关的辅助信息
   */
  @Override
  public void channelActive(final ChannelHandlerContext ctx) throws Exception {
    // 记录连接信息日志
    log.info("netty connected({}) from {} to {}", ctx.channel().id(),
        ctx.channel().remoteAddress(), ctx.channel().localAddress());
    // 将新建立的通道添加到通道映射中
    channels.put(ctx.channel().id(), ctx.channel());
    // 调用消息处理器处理通道打开事件
    this.handler.handleChannelOpen(ctx.channel());
    // 调用父类实现以处理其他逻辑
    super.channelActive(ctx);
  }

  /**
   * 当通道变得不活跃时（例如，连接断开后）被调用
   *
   * @param ctx 上下文对象，包含通道及其相关的辅助信息
   */
  @Override
  public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
    // 记录断开连接信息日志
    log.info("netty disconnected({}) from {} to {}", ctx.channel().id(),
        ctx.channel().remoteAddress(), ctx.channel().localAddress());
    // 从通道映射中移除已断开连接的通道
    channels.remove(ctx.channel().id());
    // 调用消息处理器处理通道关闭事件
    this.handler.handleChannelClose(ctx.channel());
    // 调用父类实现以处理其他逻辑
    super.channelInactive(ctx);
  }

  /**
   * 当捕获到未处理的异常时被调用
   *
   * @param ctx 上下文对象，包含通道及其相关的辅助信息
   * @param cause 引发的异常
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    // 记录异常日志
    log.error("netty server handler caught exception", cause);
    // 关闭通道以避免进一步的通信问题
    ctx.close();
  }
}

