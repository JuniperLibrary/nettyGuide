package com.uin.netty.reactor.masterslavereactormultithrea;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class Client {

  public static void main(String[] args) {
    try (SocketChannel socketChannel = SocketChannel.open()) {
      socketChannel.connect(new InetSocketAddress("localhost", 9090));
      socketChannel.configureBlocking(true);
      log.info("Connected to server.");

      Scanner scanner = new Scanner(System.in);
      ByteBuffer buffer = ByteBuffer.allocate(1024);

      while (true) {
        log.info("Input message: ");
        String message = scanner.nextLine();
        if ("exit".equalsIgnoreCase(message)) {
          break;
        }
        buffer.clear();
        buffer.put(message.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        socketChannel.write(buffer);

        buffer.clear();
        int bytesRead = socketChannel.read(buffer);
        if (bytesRead == -1) {
          break;
        }
        buffer.flip();
        String response = StandardCharsets.UTF_8.decode(buffer).toString();
        log.info("Server response: {}", response);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
