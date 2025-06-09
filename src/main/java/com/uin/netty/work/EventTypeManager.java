package com.uin.netty.work;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventTypeManager {

  public static final int TEST = 1;
  public static final int HEARTBEAT = 2;
  public static final int SUBSCRIBE_WITH_EVENT_TYPE = 11;
  public static final int SUBSCRIBE_WITH_EVENT_TYPE_AND_TOPIC = 12;
  public static final int REGISTER_WITH_NODE = 13;

  private static Map<Integer, String> eventNameMap = new ConcurrentHashMap<>();
  private static Map<String, Integer> eventStringMap = new ConcurrentHashMap<>();
  private static Map<Integer, Class> eventPayloadClassMap = new ConcurrentHashMap<>();

  static {
    addEventType(TEST, "TEST", String.class);
    addEventType(HEARTBEAT, "HEARTBEAT", Integer.class);
    addEventType(SUBSCRIBE_WITH_EVENT_TYPE, "SUBSCRIBE_WITH_EVENT_TYPE", Integer.class);
    addEventType(SUBSCRIBE_WITH_EVENT_TYPE_AND_TOPIC,
        "SUBSCRIBE_WITH_EVENT_TYPE_AND_TOPIC", Long.class);
    addEventType(REGISTER_WITH_NODE, "REGISTER_WITH_NODE", String.class);
  }

  public static void addEventType(int eventType, String eventName, Class clazz) {
    eventNameMap.put(eventType, eventName);
    eventStringMap.put(eventName, eventType);
    eventPayloadClassMap.put(eventType, clazz);
  }

  public static String getEventName(int eventType) {
    return eventNameMap.get(eventType);
  }

  public static Integer getEventFromName(String eventType) {
    return eventStringMap.get(eventType);
  }

  public static Class getEventType(int eventType) {
    return eventPayloadClassMap.get(eventType);
  }

}
