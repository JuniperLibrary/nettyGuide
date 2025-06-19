package com.uin.netty.reactor.masterslavereactormultithrea;

import java.io.*;

public class Server {

  public static void main(String[] args) throws IOException {
    int subReactorCount = 2;
    SubReactor[] subReactors = new SubReactor[subReactorCount];
    Thread[] threads = new Thread[subReactorCount];

    for (int i = 0; i < subReactorCount; i++) {
      subReactors[i] = new SubReactor();
      threads[i] = new Thread(subReactors[i], "SubReactor-" + i);
      threads[i].start();
    }

    MainReactor mainReactor = new MainReactor(9090, subReactors);
    new Thread(mainReactor, "MainReactor").start();
  }
}
