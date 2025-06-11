package com.uin.netty.nativeio.bio;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class BIOServer {

  // 调试命令
  // telnet 127.0.0.1 6666
  // ctrl + ]
  // send message

  public static void main(String[] args) throws Exception {
    // Executors.newCachedThreadPool();
    // 有任务就创建线程，空闲线程会被复用
    // 空闲线程60秒后会被回收
    // 适用于大量短期、小任务的场景
    // 线程最大数量是 Integer.MAX_VALUE，理论上几乎无限（不加限制可能导致 OOM）
//    return new ThreadPoolExecutor(
//        0,                     // corePoolSize
//        Integer.MAX_VALUE,     // maximumPoolSize
//        60L,                   // keepAliveTime
//        TimeUnit.SECONDS,      // time unit
//        new SynchronousQueue<Runnable>() // workQueue
//    );
    // corePoolSize = 0：核心线程数为 0，意味着没有任务就不会保留线程。
    // SynchronousQueue：每个任务都直接交给一个线程执行，不缓存任务。
    // maximumPoolSize = Integer.MAX_VALUE：最大线程数极大。
    ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
    ServerSocket serverSocket = new ServerSocket(6666);
    System.out.println("服务器启动了");
    while (true) {
      System.out.println("线程信息id = " + Thread.currentThread().getId() + "，名字 = " + Thread.currentThread().getName());
      System.out.println("等待连接....");
      // 会阻塞
      final Socket socket = serverSocket.accept();
      System.out.println("连接到一个客户端");
      newCachedThreadPool.execute(() -> {
        handler(socket);
      });
    }
  }

  public static void handler(Socket socket) {
    try {
      System.out.println("线程信息id = " + Thread.currentThread().getId() + "名字 = " + Thread.currentThread().getName());
      byte[] bytes = new byte[1024];
      //通过socket获取输入流
      InputStream inputStream = socket.getInputStream();
      while (true) {
        System.out.println("线程信息id = " + Thread.currentThread().getId() + "名字 = " + Thread.currentThread().getName());
        System.out.println("read....");
        int read = inputStream.read(bytes);
        if (read != -1) {
          System.out.println(new String(bytes, 0, read));//输出客户端发送的数据
        } else {
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.out.println("关闭和client的连接");
      try {
        socket.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
