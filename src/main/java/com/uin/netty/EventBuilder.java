package com.uin.netty;

import com.alibaba.fastjson.JSONObject;

public class EventBuilder {

  public static Event buildEvent(int eventType, String source, Object payload) {
    Event event = new Event();
    event.setEventType(eventType);
    event.setSource(source);
    event.setMessageId(IdGenerator.nextId());
//    event.setTarget(target);
    event.setPayload(payload);
    return event;
  }

  public static Event buildEvent(int eventType, String source, Object payload, int topic) {
    Event event = new Event();
    event.setEventType(eventType);
    event.setSource(source);
    event.setMessageId(IdGenerator.nextId());
//    event.setTarget(target);
    event.setPayload(payload);
    event.setTopic(topic);
    return event;
  }

  public static Event buildEvent(int eventType, String source, Object payload, Long traceId) {
    Event event = new Event();
    event.setEventType(eventType);
    event.setSource(source);
    event.setMessageId(IdGenerator.nextId());
//    event.setTarget(target);
    event.setPayload(payload);
    event.setTraceId(traceId);
    return event;
  }

  public static Event buildEventFromJson(String json) {
    JSONObject obj = JSONObject.parseObject(json);
    int eventType = obj.getInteger("eventType");
    String source = obj.getString("source");
    int messageId = obj.getInteger("messageId");
    String target = obj.getString("target");
    String payloadValue = obj.getString("payload");
    int topic = obj.getInteger("topic");
    Object payload = null;
    Class clazz = EventTypeManager.getEventType(eventType);
    if (clazz != null) {
      if (clazz.equals(String.class)) {
        payload = payloadValue;
      } else {
        payload = JSONObject.parseObject(payloadValue, clazz);
      }
    } else {
      return null;
    }

    Event event = new Event();
    event.setEventType(eventType);
    event.setSource(source);
    event.setMessageId(messageId);
    event.setTarget(target);
    event.setPayload(payload);
    event.setTopic(topic);
    return event;
  }
}
