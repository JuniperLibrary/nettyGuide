package com.uin.netty.work.event;

import com.codahale.metrics.*;
import java.io.*;
import java.util.concurrent.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public abstract class AbstractEventHandler {

  @Getter
  @Setter
  private boolean isOuterSendHandler=false;

  private EventBus eventBus;
  private final LinkedBlockingQueue<Event> queue;

  @Getter
  private final String name;

  public AbstractEventHandler(String name) {
    this(name, 1);
  }

  public AbstractEventHandler(String name, int n) {
    this.name = name;
    this.queue = new LinkedBlockingQueue<>();
    MetricsMarker.setGauge(name, "QueueSize", (Gauge<Integer>) queue::size);
    for (int i = 0; i < n; i++) {
      QitThreadFactory.startThread(String.format("event-handler-%s-%d", name, i), () -> {
        while (!Thread.currentThread().isInterrupted()) {
          try {
            Event event = queue.take();
            MetricsMarker.decCounter(name, EventTypeManager.getEventName(event.getEventType()), 1);

            Timer.Context context = MetricsMarker
                .startTimer(name, EventTypeManager.getEventName(event.getEventType()));
            try {
              AbstractEventHandler.this.handleEvent(event);
            } catch (Throwable e) {
              log.error(e.getMessage(), e);
            } finally {
              if (context != null) {
                context.close();
              }
            }
          } catch (Throwable e) {
            log.error(e.getMessage(), e);
          }
        }
      });
    }
  }

  public AbstractEventHandler(String name, EventBus eventBus) {
    this(name, eventBus, 1);
  }

  public AbstractEventHandler(String name, EventBus eventBus, int n) {
    this.eventBus = eventBus;
    this.name = name;
    this.queue = new LinkedBlockingQueue<>();
    MetricsMarker.setGauge(name, "QueueSize", (Gauge<Integer>) queue::size);
    for (int i = 0; i < n; i++) {
      QitThreadFactory.startThread(String.format("event-handler-%s-%d", name, i), () -> {
        while (!Thread.currentThread().isInterrupted()) {
          try {
            Event event = queue.take();
            MetricsMarker.decCounter(name, EventTypeManager.getEventName(event.getEventType()), 1);

            Timer.Context context = MetricsMarker
                .startTimer(name, EventTypeManager.getEventName(event.getEventType()));
            try {
              AbstractEventHandler.this.handleEvent(event);
            } catch (Throwable e) {
              log.error(e.getMessage(), e);
            } finally {
              if (context != null) {
                context.stop();
              }
            }
          } catch (Throwable e) {
            log.error(e.getMessage(), e);
          }
        }
      });
    }
  }

  public void setEventBus(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  protected void postEvent(Event event) {
    eventBus.postEvent(event);
  }

  protected void registerEventHandler(int eventType, int priority) {
    eventBus.registerEventHandler(eventType, this, priority);
  }

  protected void unregisterEventHandler() {
    eventBus.unregisterEventHandler(this);
  }

  public void processEvent(Event event) {
    try {
      MetricsMarker.incCounter(name, EventTypeManager.getEventName(event.getEventType()), 1);
      queue.put(event);
      MetricsMarker.markMeter(name, EventTypeManager.getEventName(event.getEventType()), 1);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error(e.getMessage(), e);
    }
  }

  public abstract void handleEvent(Event event) throws UnsupportedEncodingException;

  public boolean isHbAvailableHandler(){
    return true;
  }
}
