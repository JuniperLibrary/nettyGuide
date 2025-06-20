package com.uin.netty.sgg.taskqueue;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TaskQueueHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String received = msg.toString();
        log.info("接收到数据: {}", received);

        // 异步提交到当前 Channel 对应的 EventLoop 的 TaskQueue 中
        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(2000); // 模拟耗时任务
                log.info("异步任务处理完成（模拟耗时操作）: {}", received);
                ctx.writeAndFlush("处理完成: " + received + "\n");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(4000); // 模拟耗时任务
                log.info("异步任务处理完成（模拟耗时操作）: {}", received);
                ctx.writeAndFlush("处理完成: " + received + "\n");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        ctx.channel().eventLoop().schedule(() -> {
            try {
                Thread.sleep(4000); // 模拟耗时任务
                log.info("异步定时任务处理完成（模拟耗时操作）: {}", received);
                ctx.writeAndFlush("处理完成: " + received + "\n");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 6, TimeUnit.SECONDS);

        // 主线程继续处理，不阻塞
        log.info("主线程继续处理 I/O，不被阻塞");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
}

