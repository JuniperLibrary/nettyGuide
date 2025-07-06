package com.uin.netty.work.event;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import org.springframework.stereotype.Component;

@Component
public class EventBus {

  private final Map<Integer, List<EventHandlerWithPriority>> handlers;

  public EventBus() {
    handlers = new ConcurrentHashMap<>();
  }

  public void registerEventHandler(int eventType, AbstractEventHandler eventHandler,
      int priority) {
    if (!handlers.containsKey(eventType)) {
      handlers.put(eventType, new ArrayList<>());
    }
    List<EventHandlerWithPriority> list = handlers.get(eventType);
    list.add(new EventHandlerWithPriority(eventHandler, priority));
    list.sort(Comparator.comparingInt(o -> o.priority));
  }

  public void unregisterEventHandler(AbstractEventHandler eventHandler) {
    handlers.values().forEach(hs -> hs.removeIf(Predicate.isEqual(eventHandler)));
  }

  public void postEvent(Event event) {
    List<EventHandlerWithPriority> list = handlers.get(event.getEventType());
    if (list != null) {
      for (EventHandlerWithPriority handler : list) {
        if(!event.isInnerHandle()){
          if(handler.eventHandler.isOuterSendHandler()){
            handler.eventHandler.processEvent(event);
          }
          continue;
        }
        if(handler.eventHandler.isHbAvailableHandler()) {
          handler.eventHandler.processEvent(event);
        }
      }
    }
  }

  static class EventHandlerWithPriority {

    private final AbstractEventHandler eventHandler;
    private final int priority;

    public EventHandlerWithPriority(AbstractEventHandler eventHandler, int priority) {
      this.eventHandler = eventHandler;
      this.priority = priority;
    }
  }
}
