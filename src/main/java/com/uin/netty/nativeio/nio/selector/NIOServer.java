package com.uin.netty.nativeio.nio.selector;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class NIOServer {

  public static void main(String[] args) throws Exception {
    //创建一个服务端channel
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    //创建一个selector
    Selector selector = Selector.open();
    //绑定一个端口，在服务端监听
    serverSocketChannel.socket().bind(new InetSocketAddress(6666));
    //设置为非阻塞
    serverSocketChannel.configureBlocking(false);
    //serverSocketChannel注册到selector,并设置关心事件
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

    // System.out.println("注册SelectionKey的数量" + selector.keys().size());

    //循环等待客户链接
    while (true) {//一次循环解决一个事件集合里的全部事件，下次循环会再继续监听
      if (selector.select(1000) == 0) {//无事发生,继续循环等待
        // selector.selectedKeys().size() // 有事件发生的事件的数量
        log.info("服务器等待了一秒，无事发生，当前以注册的selectionKey数量为：{}", selector.keys().size());
        continue;
      }
      log.info("selectionKeys 数量：{}", selector.selectedKeys().size());
      //返回》0，有事发生,获取客户端发生关注事件的集合
      //通过selectionKeys可以反向获取Channel
      Set<SelectionKey> selectionKeys = selector.selectedKeys();
      //遍历selectionKeys，获取当前事件集
      Iterator<SelectionKey> Keyiterator = selectionKeys.iterator();
      while (Keyiterator.hasNext()) {
        //获取当前事件
        SelectionKey selectionKey = Keyiterator.next();
        //查看key发生的事件并作相应的处理
        if (selectionKey.isAcceptable()) {//有新客户端链接
          //为这个客户端生成一个channel
          SocketChannel socketchannel = serverSocketChannel.accept();
          socketchannel.configureBlocking(false);
          log.info("客户端连接成功={}", socketchannel.hashCode());
          //将当前channel注册到selector,并关心这个事件有没有发生读事件OP_READ，同时给channel关联一个buffer
          socketchannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
        }
        if (selectionKey.isReadable()) {
          //发生了读事件,通过key反向得到channel和绑定的buffer
          SocketChannel socketchannel = (SocketChannel) selectionKey.channel();
          //获取与该channel关联的buffer，在和连接时就已经绑定了，40行处
          ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
          socketchannel.read(buffer);//从客户端读到的数据
          log.info("from 客户端 : {}", new String(buffer.array()));
          buffer.clear();
        }
      }
      //及时将当前的SelectionKey,防止操作
      Keyiterator.remove();
    }
  }
}
