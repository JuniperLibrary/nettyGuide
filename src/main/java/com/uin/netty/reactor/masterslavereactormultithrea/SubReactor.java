package com.uin.netty.reactor.masterslavereactormultithrea;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SubReactor implements Runnable {

  private final Selector selector;

  public SubReactor() throws IOException {
    this.selector = Selector.open();
  }

  public void register(SocketChannel channel) throws IOException {
    selector.wakeup();
    channel.configureBlocking(false);
    channel.register(selector, SelectionKey.OP_READ, new EchoHandler(channel));
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
          if (key.isReadable()) {
            EchoHandler handler = (EchoHandler) key.attachment();
            handler.handle();
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
