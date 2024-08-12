package com.uin.netty;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadFactory {

  private static AtomicInteger total = new AtomicInteger(0);
  private static int current = 0;
  private static int quit_normal = 0;
  private static int quit_abnormal = 0;

  public static Thread startThread(String name, Runnable runnable) {
    return startThread(name, runnable, false);
  }

  public static Thread startThread(String name, Runnable runnable, boolean daemon) {
    int index = total.incrementAndGet();
    String threadName = String.format("%04d-%s", index % 10000, name);
    Thread thread = new Thread(() -> {
      String tname = threadName;
      current++;
      log.info("Thread({}) started(total: {})", tname, current);
      try {
        runnable.run();
        quit_normal++;
        log.info("Thread({}) quit normally(total: {})", tname, quit_normal);
      } catch (Throwable e) {
        quit_abnormal++;
        log.error(String.format("Thread(%s) quit abnormally(total: %d)", tname, quit_abnormal), e);
      }
      current--;
    }, threadName);
    thread.setDaemon(daemon);
    thread.start();
    return thread;
  }
}
