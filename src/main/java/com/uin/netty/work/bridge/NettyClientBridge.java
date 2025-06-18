package com.uin.netty.work.bridge;

import com.uin.netty.work.*;
import com.uin.netty.work.event.*;
import io.netty.channel.*;
import lombok.extern.slf4j.*;

@Slf4j
public class NettyClientBridge extends AbstractEventHandler implements NettyMessageHandler {

  private final NettyClient client;

  private Channel channel;

  public NettyClientBridge(EventBus eventBus, String host, int port, String name) {
    super(name, eventBus);
    client = new NettyClient(host, port, this);
    super.setOuterSendHandler(true);
  }

  public void connect() {
    client.connect();
  }

  public void disconnect() {
    client.setClosed(true);
    if (channel != null) {
      channel.disconnect();
    }
  }

  public void registerEventHandler(int eventType) {
    this.registerEventHandler(eventType, 0);
  }

  public void unregisterEventHandler() {
    super.unregisterEventHandler();
  }

  @Override
  public void handleEvent(Event event) {
    NettyUtils.sendEvent(this.channel, getName(), event);
  }

  @Override
  public void handleMessage(Channel channel, byte[] msg) {
    String message = new String(msg);
    log.info("recv message: {}", message);
    Event event = EventBuilder.buildEventFromJson(message);
    this.postEvent(event);
  }

  @Override
  public void handleChannelOpen(Channel channel) {
    log.info("channel open({}): {}", channel.id().asShortText(), channel.id().asLongText());
    this.channel = channel;
  }

  @Override
  public void handleChannelClose(Channel channel) {
    log.info("channel close({}): {}", channel.id().asShortText(), channel.id().asLongText());
    this.channel = null;
  }
}
