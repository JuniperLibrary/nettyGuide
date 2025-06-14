package com.uin.netty.nativeio.groupchat;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class GroupChatClient {

  private final static String HOST = "127.0.0.1";

  private final static int PORT = 6667;

  private final Selector selector;

  private final SocketChannel socketChannel;

  private final String userName;

  public GroupChatClient() {
    try {
      selector = Selector.open();
      socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
      socketChannel.configureBlocking(false);
      socketChannel.register(selector, SelectionKey.OP_READ);
      userName = socketChannel.getLocalAddress().toString().substring(1);
      log.info("{} is ok ", userName);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    GroupChatClient groupChatClient = new GroupChatClient();
    new Thread(() -> {
      while (true) {
        groupChatClient.readInfo();
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }).start();

    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNextLine()) {
      String s = scanner.nextLine();
      groupChatClient.sendInfo(s);
    }
  }

  private void sendInfo(String s) {
    s = userName + "说 ： " + s;
    try {
      socketChannel.write(ByteBuffer.wrap(s.getBytes()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void readInfo() {
    try {
      int readChannels = selector.select();
      if (readChannels > 0) {
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
          SelectionKey key = iterator.next();
          if (key.isReadable()) {
            SocketChannel selectableChannel = (SocketChannel) key.channel();
            ByteBuffer allocate = ByteBuffer.allocate(1024);
            selectableChannel.read(allocate);
            String trim = new String(allocate.array()).trim();
            log.info("客户端读到的消息：{}", trim);
          }
        }
        iterator.remove();
      } else {
        log.info("没有可用的通道.....");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
