package com.uin.netty.work.bridge;

import cn.hutool.core.util.*;
import com.uin.netty.work.*;
import com.uin.netty.work.event.*;
import io.netty.channel.*;
import java.util.*;
import java.util.concurrent.*;
import lombok.extern.slf4j.*;

@Slf4j
public class NettyServerBridge extends AbstractEventHandler implements NettyMessageHandler {

  private final NettyServer server;

  private final Map<String, Channel> channels = new ConcurrentHashMap<>();

  public NettyServerBridge(EventBus eventBus, int port, String name) {
    super(name, eventBus);
    server = new NettyServer(port, name, this);
    super.setOuterSendHandler(true);
  }

  public void start() {
    server.start();
  }

  public void stop() {
    super.unregisterEventHandler();
    channels.values().forEach(ChannelOutboundInvoker::close);
    server.stop();
  }

  public void registerEventHandler(int eventType) {
    this.registerEventHandler(eventType, 0);
  }

  public void unregisterEventHandler() {
    super.unregisterEventHandler();
  }

  @Override
  public void handleEvent(Event event) {
    String channelIdText = event.getTarget();
    if (StrUtil.isEmpty(channelIdText)) {
      // broadcast
      for (Channel channel : channels.values()) {
        NettyUtils.sendEvent(channel, getName(), event);
      }
    } else {
      Channel channel = channels.get(channelIdText);
      if (channel != null) {
        NettyUtils.sendEvent(channel, getName(), event);
      } else {
        log.warn("channel not found: {}", channelIdText);
      }
    }
  }

  @Override
  public void handleMessage(Channel channel, byte[] msg) {
    String message = new String(msg);
    log.info("recv message: {}", message);
    Event event = EventBuilder.buildEventFromJson(message);
    event.setSource(channel.id().asShortText());
    this.postEvent(event);
  }

  @Override
  public void handleChannelOpen(Channel channel) {
    log.info("channel open({}): {}", channel.id().asShortText(), channel.id().asLongText());
    channels.put(channel.id().asShortText(), channel);
  }

  @Override
  public void handleChannelClose(Channel channel) {
    log.info("channel close({}): {}", channel.id().asShortText(), channel.id().asLongText());
    channels.remove(channel.id().asShortText());
  }
}
