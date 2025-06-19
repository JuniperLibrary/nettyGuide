package com.uin.netty.reactor.masterslavereactormultithrea;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.*;

@Slf4j
public class EchoHandler {

  private final SocketChannel channel;
  private final ByteBuffer buffer = ByteBuffer.allocate(1024);

  public EchoHandler(SocketChannel channel) {
    this.channel = channel;
  }

  public void handle() throws IOException {
    int read = channel.read(buffer);
    if (read == -1) {
      channel.close();
      return;
    }
    buffer.flip();
    String received = StandardCharsets.UTF_8.decode(buffer).toString().trim();
    log.info("Received from client: {}" , received);

    // 回显 + 服务端主动回复一条消息
    String response = "Server received: " + received + "\n";
    channel.write(ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8)));

    buffer.clear();
  }
}
