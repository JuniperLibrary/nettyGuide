package com.uin.netty.reactor.masterslavereactormultithrea;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class MainReactor implements Runnable {

  private final ServerSocketChannel serverChannel;
  private final Selector selector;
  private final SubReactor[] subReactors;
  private int next = 0;

  public MainReactor(int port, SubReactor[] subReactors) throws IOException {
    this.serverChannel = ServerSocketChannel.open();
    serverChannel.configureBlocking(false);
    serverChannel.bind(new InetSocketAddress(port));

    this.selector = Selector.open();
    serverChannel.register(selector, SelectionKey.OP_ACCEPT);

    this.subReactors = subReactors;
  }

  @Override
  public void run() {
    try {
      while (true) {
        selector.select();
        Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
        while (iter.hasNext()) {
          SelectionKey key = iter.next();
          iter.remove();
          if (key.isAcceptable()) {
            SocketChannel client = serverChannel.accept();
            if (client != null) {
              client.configureBlocking(false);
              SubReactor subReactor = subReactors[next];
              next = (next + 1) % subReactors.length;
              subReactor.register(client);
              log.info("Accepted: {}", client.getRemoteAddress());
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
