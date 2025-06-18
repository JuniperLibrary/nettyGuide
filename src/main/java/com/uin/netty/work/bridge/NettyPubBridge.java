package com.uin.netty.work.bridge;

import cn.hutool.core.util.*;
import com.alibaba.fastjson.*;
import com.uin.netty.work.*;
import com.uin.netty.work.event.*;
import io.netty.buffer.*;
import io.netty.channel.*;
import java.util.*;
import java.util.Map.*;
import java.util.concurrent.*;
import lombok.extern.slf4j.*;

@Slf4j
public class NettyPubBridge extends AbstractEventHandler implements NettyMessageHandler {

  private final NettyPubServer server;

  private final Map<Integer, Set<ChannelId>> subscribeWithEventType = new ConcurrentHashMap<>();
  private final Map<Long, Set<ChannelId>> subscribeWithEventTypeAndTopic = new ConcurrentHashMap<>();
  private final Map<ChannelId, Channel> channels = new ConcurrentHashMap<>();
  private final Map<String, ChannelId> registerNodes = new ConcurrentHashMap<>();

  public NettyPubBridge(EventBus eventBus, String host, int port, String name) {
    super(name, eventBus);
    server = new NettyPubServer(host, port, name, this);
    super.setOuterSendHandler(true);
  }

  public void start() {
    server.start();

    // 同步等待启动成功
    while (true) {
      if (server.isStarted()) {
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
    if (Event.NETWORK_SUB.equals(event.getSource())) {
      return;
    }

    Integer eventType = event.getEventType();
    Long eventTypeAndTopic = (long) event.getEventType() << 32 + event.getTopic();
    Set<ChannelId> channelIdSet = new HashSet<>();
    if (subscribeWithEventType.containsKey(eventType)) {
      channelIdSet.addAll(subscribeWithEventType.get(eventType));
    }
    if (subscribeWithEventTypeAndTopic.containsKey(eventTypeAndTopic)) {
      channelIdSet.addAll(subscribeWithEventTypeAndTopic.get(eventTypeAndTopic));
    }

    if (!channelIdSet.isEmpty()) {
      String json = JSONObject.toJSONString(event);
      int hashCode = event.getTopic();
      if (0 != hashCode && registerNodes.size() > 0) {
        // 根据event的哈希值从zk中获取负载处理节点
//        String nodeName = ZookeeperUtil.getServiceByHash(hashCode);
        String nodeName = "";
        if (StrUtil.isBlank(nodeName) || null == registerNodes.get(nodeName)) {
          log.warn("handle event write message fail,zookeeper node is null");
          return;
        }
        // 获取节点对应的channel，并写入消息
        ChannelId channelId = registerNodes.get(nodeName);
        log.info("handle event: zookeeper node {} with channel {} write message", nodeName,
            channelId);
        writeMessage(json, channelId);
      } else {
        // 广播
        for (ChannelId channelId : channelIdSet) {
          writeMessage(json, channelId);
        }
      }
    }
  }

  private void writeMessage(String json, ChannelId channelId) {
    Channel channel = channels.get(channelId);
    if (channel == null) {
      log.warn("channel({}) not exist", channelId);
      return;
    }
    log.debug("write message({}): {}bytes, {}", channel.id(), json.getBytes().length, json);

    if (json.getBytes().length > MAX_MESSAGE_SIZE) {
      log.warn("message length exceed({}): {}bytes, {}", channel.id(), json.getBytes().length,
          json.substring(0, 10240));
      return;
    }

    ByteBuf buf = Unpooled.buffer();
    buf.writeShort(json.getBytes().length);
    buf.writeBytes(json.getBytes());
    ChannelFuture future = channel.writeAndFlush(buf);
    future.addListener((ChannelFutureListener) f -> {
      if (!f.isSuccess()) {
        log.warn("write message({}) failed", channel.id());
      }
    });
  }

  @Override
  public void handleMessage(Channel channel, byte[] msg) {
    String message = new String(msg);
    log.debug("recv message: {}", message);
    Event event = EventBuilder.buildEventFromJson(message);
    if (event == null) {
      log.warn("event parse failed: {}", message);
      return;
    }
    if (event.getEventType() == EventTypeManager.SUBSCRIBE_WITH_EVENT_TYPE) {
      Integer eventType = (Integer) event.getPayload();
      log.info("netty recv event sub: {}", eventType);
      subscribeWithEventType
          .computeIfAbsent(eventType, k -> new HashSet<>())
          .add(channel.id());
    } else if (event.getEventType() == EventTypeManager.SUBSCRIBE_WITH_EVENT_TYPE_AND_TOPIC) {
      Long eventTypeAndTopic = (Long) event.getPayload();
      log.info("netty recv event sub: {}, {}",
          eventTypeAndTopic >> 32, eventTypeAndTopic & 0xffffffff);
      subscribeWithEventTypeAndTopic
          .computeIfAbsent(eventTypeAndTopic, k -> new HashSet<>())
          .add(channel.id());
    } else if (event.getEventType() == EventTypeManager.REGISTER_WITH_NODE) {
      String nodeName = (String) event.getPayload();
      log.info("netty recv event register node: {}", nodeName);
      if (StrUtil.isNotBlank(nodeName)) {
        registerNodes.put(nodeName, channel.id());
      }
    } else if (event.getEventType() == EventTypeManager.HEARTBEAT) {
      if (log.isDebugEnabled()) {
        log.debug("netty server recive event heartbeat. {}", event);
      }
      String nodeName = "";
      for (Entry<String, ChannelId> entry : registerNodes.entrySet()) {
        if (entry.getValue().equals(channel.id())) {
          nodeName = entry.getKey();
          break;
        }
      }
      // 响应心跳
      NettyUtils.sendEvent(channel, nodeName,
          EventBuilder.buildEvent(EventTypeManager.HEARTBEAT, "netty-server", 0));
    }
  }

  @Override
  public void handleChannelOpen(Channel channel) {
    log.info("channel open({}): {}", channel.id().asShortText(), channel.id().asLongText());
    channels.put(channel.id(), channel);
  }

  @Override
  public void handleChannelClose(Channel channel) {
    log.info("channel close({}): {}", channel.id().asShortText(), channel.id().asLongText());
    for (Set<ChannelId> idSet : subscribeWithEventType.values()) {
      idSet.remove(channel.id());
    }
    for (Set<ChannelId> idSet : subscribeWithEventTypeAndTopic.values()) {
      idSet.remove(channel.id());
    }
    channels.remove(channel.id());
  }
}
