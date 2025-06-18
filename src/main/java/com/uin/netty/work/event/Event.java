package com.uin.netty.work.event;

import com.uin.netty.work.rocketmq.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event extends BaseMqMessage {

  public static final String NETWORK_SUB = "network-sub";

  // 消息类型
  private int eventType;

  // 消息Topic
  private int topic;

  // 消息发送者
  private String source;

  // 由消息发送者维护(从1开始计数)
  private long messageId;

  // 消息接受者
  private String target;

  // 消息载荷
  private Object payload;

  // 业务跟踪ID
  private Long traceId;

}
