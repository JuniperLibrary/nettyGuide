package com.uin.netty.nativeio.groupchat;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class GroupChatServer {

  private final Selector selector;

  private final ServerSocketChannel serverSocketChannel;

  private static final int PORT = 6667;

  public GroupChatServer() {
    try {
      selector = Selector.open();
      serverSocketChannel = ServerSocketChannel.open();

      serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
      serverSocketChannel.configureBlocking(false);
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public void listen() {
    while (true) {
      int select = 0;
      try {
        select = selector.select();
        if (select > 0) {
          Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
          while (iterator.hasNext()) {
            SelectionKey selectionKey = iterator.next();
            if (selectionKey.isAcceptable()) {
              SocketChannel socketChannel = serverSocketChannel.accept();
              socketChannel.configureBlocking(false);
              socketChannel.register(selector, SelectionKey.OP_READ);
              log.info("{} 上线", socketChannel.getRemoteAddress());
            }

            if (selectionKey.isReadable()) {
              readData(selectionKey);
            }

            iterator.remove();
          }
        } else {
          log.info("等待......");
        }

      } catch (IOException e) {
        throw new RuntimeException(e);
      }

    }
  }

  private void readData(SelectionKey selectionKey) {
    SocketChannel socketChannel = null;
    try {
      socketChannel = (SocketChannel) selectionKey.channel();
      ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
      int count = socketChannel.read(byteBuffer);

      if (count > 0) {
        String message = new String(byteBuffer.array()).trim();
        log.info("form 客户端消息：{}", message);
        sendInfoToOtherClients(message, socketChannel);
      }
    } catch (IOException e) {
      try {
        log.info("{}-离线了..", socketChannel.getRemoteAddress());
        //取消注册
        selectionKey.cancel();
        //关闭通道
        socketChannel.close();
      } catch (IOException e2) {
        e2.printStackTrace();
      }
    }
  }

  private void sendInfoToOtherClients(String message, SocketChannel socketChannel) {
    log.info("服务器转发消息中......");
    try {
      for (SelectionKey key : selector.keys()) {
        SelectableChannel targetChannel = key.channel();
        if (targetChannel instanceof SocketChannel && targetChannel != socketChannel) {
          SocketChannel socketChannel1 = (SocketChannel) targetChannel;
          ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes());
          socketChannel1.write(byteBuffer);
          log.info("服务器转发消息成功");
        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    new GroupChatServer().listen();
  }
}
