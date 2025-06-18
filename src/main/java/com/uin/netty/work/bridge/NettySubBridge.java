package com.uin.netty.work.bridge;

import com.qit.rates.common.event.event.*;
import com.qit.rates.common.event.netty.*;
import io.netty.channel.*;
import java.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class NettySubBridge extends AbstractEventHandler implements NettyMessageHandler {

  private final NettySubClient client;
  private final Set<Integer> subscribeEventType = new HashSet<>();
  private final Set<Long> subscribeEventTypeWithTopic = new HashSet<>();
  private String nodeNameRigister = "";

  private Channel channel;

  public NettySubBridge(EventBus eventBus, String host, int port, String name) {
    super(name, eventBus);
    client = new NettySubClient(host, port, name, this);
  }

  public void start() {
    client.start();

    // 同步等待启动成功
    while (true) {
      if (channel != null && channel.isActive()) {
        return;
      }

      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        log.error("", e);
      }
    }
  }

  public void stop() {
    super.unregisterEventHandler();

    client.setClosed(true);
    if (channel != null) {
      channel.disconnect();
    }
  }

  public void unregisterEventHandler() {
    super.unregisterEventHandler();
  }

  public void subscribe(Integer eventType) {
    subscribeEventType.add(eventType);
    log.info("netty sub event type: {}", eventType);
    NettyUtils.sendEvent(channel, getName(),
        new Event(EventTypeManager.SUBSCRIBE_WITH_EVENT_TYPE, 0, "sub", 0,
            "pub", eventType, -1L));
  }

  public void subscribeWithTopic(Long eventTypeAndTopic) {
    subscribeEventTypeWithTopic.add(eventTypeAndTopic);
    log.info("netty sub event type: {}, {}", eventTypeAndTopic >> 32,
        eventTypeAndTopic & 0xffffffff);
    NettyUtils.sendEvent(channel, getName(),
        new Event(EventTypeManager.SUBSCRIBE_WITH_EVENT_TYPE_AND_TOPIC, 0, "sub", 0, "pub",
            eventTypeAndTopic, -1L));
  }

  public void subscribeWithTopic(int eventType, int topic) {
    Long eventTypeAndTopic = (long) eventType << 32 + topic;
    this.subscribeWithTopic(eventTypeAndTopic);
  }

  public void registerWithNode(String nodeName) {
    log.info("netty register with node: {}", nodeName);
    nodeNameRigister = nodeName;
    NettyUtils.sendEvent(channel, getName(),
        new Event(EventTypeManager.REGISTER_WITH_NODE, 0, "sub", 0,
            "pub", nodeName, -1L));
  }

  @Override
  public void handleEvent(Event event) {
    // do nothing
  }

  @Override
  public void handleMessage(Channel channel, byte[] msg) {
    String message = new String(msg);
    log.debug("recv message({}): {}", channel.id().asShortText(), message);
    Event event = EventBuilder.buildEventFromJson(message);
    event.setSource(Event.NETWORK_SUB);
    this.postEvent(event);
  }

  @Override
  public void handleChannelOpen(Channel channel) {
    log.info("channel open({}): {}", channel.id().asShortText(), channel.id().asLongText());

    this.channel = channel;

    // 重新订阅
    for (Integer eventType : subscribeEventType) {
      this.subscribe(eventType);
    }
    for (Long eventTypeWithTopic : subscribeEventTypeWithTopic) {
      this.subscribeWithTopic(eventTypeWithTopic);
    }
    // 重新注册
    registerWithNode(nodeNameRigister);
  }

  @Override
  public void handleChannelClose(Channel channel) {
    log.info("channel close({}): {}", channel.id().asShortText(), channel.id().asLongText());
    this.channel = null;
  }
}
