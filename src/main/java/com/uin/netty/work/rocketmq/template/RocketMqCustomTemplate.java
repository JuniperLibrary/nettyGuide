package com.uin.netty.work.rocketmq.template;

import cn.hutool.core.collection.*;
import cn.hutool.json.*;
import com.alibaba.fastjson.JSONObject;
import com.uin.netty.work.rocketmq.*;
import java.util.*;
import javax.annotation.*;
import lombok.extern.slf4j.*;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.spring.core.*;
import org.apache.rocketmq.spring.support.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.messaging.*;
import org.springframework.messaging.support.*;
import org.springframework.stereotype.*;

/**
 * RocketMQ模板类
 */
@Component
@Slf4j
public class RocketMqCustomTemplate {

  @Resource(name = "rocketMQTemplate")
  private RocketMQTemplate template;

  /**
   * 获取模板，如果封装的方法不够提供原生的使用方式
   */
  public RocketMQTemplate getTemplate() {
    return template;
  }

  @Value("${spring.application.name}")
  private String appName;

  /**
   * 构建发送消息的目的地
   *
   * @param topic topic
   * @param tag   tag
   * @return
   */
  public String buildDestination(String topic, String tag) {
    return topic + RocketMqSysConstant.DELIMITER + tag;
  }

  /**
   * 发送异步消息
   *
   * @param topic   topic
   * @param tag     tag
   * @param message 消息内容
   * @param <T>     所有 extends BaseMqMessage 的对象
   */
  public <T extends BaseMqMessage> void asyncSend(String topic, String tag, T message) {
    // 注意分隔符
    asyncSend(buildDestination(topic, tag), message);
  }

  /**
   * 发送异步消息
   *
   * @param destination 消息目的地址
   * @param message     消息内容
   * @param <T>         所有 extends BaseMqMessage 的对象
   */
  public <T extends BaseMqMessage> void asyncSend(String destination, T message) {
    // 设置业务键，此处根据公共的参数进行处理
    // 更多的其它基础业务处理...
    Message<T> sendMessage = MessageBuilder.withPayload(message)
        .setHeader(RocketMQHeaders.KEYS, message.getKey())
        .build();
    template.asyncSend(destination, sendMessage, new SendCallback() {
      @Override
      public void onSuccess(SendResult sendResult) {

      }

      @Override
      public void onException(Throwable e) {

      }
    });
    // 此处为了方便查看给日志转了json，根据选择选择日志记录方式，例如ELK采集
    log.info("[{}]异步消息[{}]", destination, JSONUtil.toJsonStr(message));
  }


  /**
   * 发送同步消息
   *
   * @param topic   topic
   * @param tag     tag
   * @param message 消息内容
   * @param <T>     所有 extends BaseMqMessage 的对象
   */
  public <T extends BaseMqMessage> SendResult syncSend(String topic, String tag, T message) {
    // 注意分隔符
    return syncSend(buildDestination(topic, tag), message);
  }

  /**
   * 发送同步消息
   *
   * @param destination 消息目的地址
   * @param message     消息内容
   * @return <T> 所有 extends BaseMqMessage 的对象
   */
  public <T extends BaseMqMessage> SendResult syncSend(String destination, T message) {
    // 设置业务键，此处根据公共的参数进行处理
    // 更多的其它基础业务处理...
    Message<T> sendMessage = MessageBuilder.withPayload(message)
        .setHeader(RocketMQHeaders.KEYS, message.getKey())
        .build();
    SendResult sendResult = template.syncSend(destination, sendMessage);
    // 此处为了方便查看给日志转了json，根据选择选择日志记录方式，例如ELK采集
    log.info("[{}]同步消息[{}]发送结果[{}]", destination, JSONUtil.toJsonStr(message), JSONUtil.toJsonStr(sendResult));
    return sendResult;
  }


  /**
   * 发送同步顺序消息
   *
   * @param destination 消息目的地址
   * @param message     消息内容
   * @param hashKey     使用此键选择队列。例如：orderId、productId
   * @return 发送消息结果
   */
  public <T extends BaseMqMessage> SendResult syncSendOrderLy(String destination, T message, String hashKey) {
    Message<T> sendMessage = MessageBuilder.withPayload(message)
        .setHeader(RocketMQHeaders.KEYS, message.getKey())
        .build();
    SendResult sendResult = template.syncSendOrderly(destination, sendMessage, hashKey);
    log.info("[{}]同步顺序消息[{}]hashKey[{}]发送结果[{}]", destination, JSONUtil.toJsonStr(message), hashKey,
        JSONUtil.toJsonStr(sendResult));
    return sendResult;
  }

  /**
   * 发送同步顺序消息
   *
   * @param topic   消息topic
   * @param tag     消息tag
   * @param message 消息体
   * @param hashKey 使用此键选择队列。例如：orderId、productId
   * @return 发送消息结果
   */
  public <T extends BaseMqMessage> SendResult syncSendOrderLy(String topic, String tag, T message, String hashKey) {
    return syncSendOrderLy(buildDestination(topic, tag), message, hashKey);
  }

  /**
   * 发送延迟消息
   *
   * @param topic          topic
   * @param tag            tag
   * @param message        消息内容
   * @param delayLevelEnum 延迟等级
   * @return <T> 所有 extends BaseMqMessage 的对象
   */
  public <T extends BaseMqMessage> SendResult delaySend(String topic, String tag, T message,
      RocketMqDelayLevelEnum delayLevelEnum) {
    return delaySend(buildDestination(topic, tag), message, delayLevelEnum);
  }

  /**
   * 发送延迟消息
   *
   * @param destination    消息目的地址
   * @param message        消息内容
   * @param delayLevelEnum 延迟等级
   * @return <T> 所有 extends BaseMqMessage 的对象
   */
  public <T extends BaseMqMessage> SendResult delaySend(String destination, T message, RocketMqDelayLevelEnum delayLevelEnum) {
    Message<T> sendMessage = MessageBuilder.withPayload(message).setHeader(RocketMQHeaders.KEYS, message.getKey()).build();
    SendResult sendResult = template.syncSend(destination, sendMessage, 3000, delayLevelEnum.getLevel());
    log.info("[{}]延迟等级[{}]消息[{}]发送结果[{}]", destination, delayLevelEnum.getLevel(), JSONUtil.toJsonStr(message),
        JSONUtil.toJsonStr(sendResult));
    return sendResult;
  }

  public <T extends BaseMqMessage> SendResult syncSendCommonOrderLy(String topic, String tag, T message,
      HashMap<String, String> header, String hashKey) {
    return syncSendCommonOrderLy(buildDestination(topic, tag), message, header, hashKey);
  }

  public <T extends BaseMqMessage> SendResult syncSendCommonOrderLy(String destination, T message,
      HashMap<String, String> header, String hashKey) {
    MessageBuilder<T> tMessageBuilder = MessageBuilder.withPayload(message);
    tMessageBuilder.setHeader(RocketMQHeaders.KEYS, message.getKey());
    if (CollUtil.isNotEmpty(header)) {
      header.forEach(tMessageBuilder::setHeader);
    }
    Message<T> sendMessage = tMessageBuilder.build();
    SendResult sendResult = template.syncSendOrderly(destination, sendMessage, hashKey);
    log.info("[{}]同步顺序消息[{}]hashKey[{}]发送结果[{}]", destination, JSONUtil.toJsonStr(message), hashKey,
        JSONUtil.toJsonStr(sendResult));
    return sendResult;
  }

  public <T extends BaseMqMessage> SendResult syncSendCommon(String destination, T message,
      HashMap<String, String> header) {
    MessageBuilder<T> tMessageBuilder = MessageBuilder.withPayload(message);
    tMessageBuilder.setHeader(RocketMQHeaders.KEYS, message.getKey());
    if (CollUtil.isNotEmpty(header)) {
      header.forEach(tMessageBuilder::setHeader);
    }
    Message<T> sendMessage = tMessageBuilder.build();
    SendResult sendResult = template.syncSend(destination, sendMessage);
    log.info("[{}]同步消息[{}],发送结果[{}]", destination, JSONUtil.toJsonStr(message), JSONUtil.toJsonStr(sendResult));
    return sendResult;
  }

  public <T extends BaseMqMessage> SendResult syncEventSendCommonOrderLy(String destination, T message,
      HashMap<String, String> header, String hashKey) {
    MessageBuilder<String> tMessageBuilder = MessageBuilder.withPayload(JSONObject.toJSONString(message));
    tMessageBuilder.setHeader(RocketMQHeaders.KEYS, message.getKey());
    if (CollUtil.isNotEmpty(header)) {
      header.forEach(tMessageBuilder::setHeader);
    }
    Message<String> sendMessage = tMessageBuilder.build();
    SendResult sendResult = template.syncSendOrderly(destination, sendMessage, hashKey);
    log.info("[{}]同步顺序消息[{}]hashKey[{}]发送结果[{}]", destination, JSONUtil.toJsonStr(message), hashKey,
        JSONUtil.toJsonStr(sendResult));
    return sendResult;
  }

  public <T extends BaseMqMessage> SendResult syncEventSendCommon(String destination, T message,
      HashMap<String, String> header) {
    MessageBuilder<String> tMessageBuilder = MessageBuilder.withPayload(JSONObject.toJSONString(message));
    tMessageBuilder.setHeader(RocketMQHeaders.KEYS, message.getKey());
    if (CollUtil.isNotEmpty(header)) {
      header.forEach(tMessageBuilder::setHeader);
    }
    Message<String> sendMessage = tMessageBuilder.build();
    SendResult sendResult = template.syncSend(destination, sendMessage);
    log.info("[{}]同步消息[{}],发送结果[{}]", destination, JSONUtil.toJsonStr(message), JSONUtil.toJsonStr(sendResult));
    return sendResult;
  }

  public <T extends BaseMqMessage> SendResult sendMQ(Object o, String eventName, String topic, String hashKey) {
    Event e = new Event();
    e.setPayload(o);
    e.setEventName(eventName);
    e.setServiceFrom(appName);
    String message = JSONObject.toJSONString(e);
    log.info("[{}]同步顺序消息[{}]hashKey[{}]发送结果[{}]", topic, JSONUtil.toJsonStr(message), hashKey, JSONUtil.toJsonStr(e));
    return template.syncSendOrderly(topic + ":" + appName, message, hashKey);
  }
}
