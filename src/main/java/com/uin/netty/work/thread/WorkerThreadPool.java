package com.uin.netty.work.thread;

import com.codahale.metrics.Timer;
import com.uin.netty.work.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkerThreadPool<T> {

  private final int threadCount;
  private final ToIntFunction<T> hashFunction;
  private final List<LinkedBlockingQueue<T>> queues = new ArrayList<>();

  public WorkerThreadPool(String name, int threadCount, Consumer<T> consumer,
      ToIntFunction<T> hashFunction) {
    this.threadCount = threadCount;
    this.hashFunction = hashFunction;
    for (int index = 0; index < threadCount; index++) {
      LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<>();
      queues.add(queue);
      ThreadFactory.startThread(String.format("%s-%d", name, index), () -> {
        while (true) {
          T obj = null;
          try {
            obj = queue.take();
            try (Timer.Context ignored = MetricsMarker
                .startTimer("threadPool", String.format("tps-%s", name))) {
              consumer.accept(obj);
            }
          } catch (Exception e) {
            log.error("take obj: {}, ", obj, e);
          }
        }
      });
    }
    MetricsMarker.setGauge("threadPool", String.format("queueSize-%s", name),
        () -> queues.stream().mapToInt(LinkedBlockingQueue::size).sum());
  }

  public void process(T obj) {
    try {
      int hash = this.hashFunction.applyAsInt(obj);
      int index = (hash % threadCount + threadCount) % threadCount;
      queues.get(index).put(obj);
    } catch (InterruptedException e) {
      log.error("process obj: {}, ", obj, e);
    } catch (Exception e) {
      log.error("process obj: {}, ", obj, e);
      throw e;
    }
  }
}
