package com.uin.netty;

import io.netty.channel.Channel;

public interface NettyMessageHandler {

  int MAX_FRAME_SIZE = 1024 * 1024;

  int MAX_MESSAGE_SIZE = 1024 * 1024 - 1024;

  void handleMessage(Channel channel, byte[] msg);

  void handleChannelOpen(Channel channel);

  void handleChannelClose(Channel channel);
}
