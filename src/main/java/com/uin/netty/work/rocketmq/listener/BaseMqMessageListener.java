package com.uin.netty.work.rocketmq.listener;

import cn.hutool.json.*;
import com.uin.netty.work.rocketmq.*;
import java.time.*;
import lombok.extern.slf4j.*;

/**
 * 抽象消息监听器，封装了所有公共处理业务，如 1、基础日志记录 2、异常处理 3、消息重试 4、警告通知
 *
 */
@Slf4j
public abstract class BaseMqMessageListener<T extends BaseMqMessage> {

  /**
   * 消息者名称
   *
   * @return 消费者名称
   */
  protected abstract String consumerName();

  /**
   * 消息处理
   *
   * @param message 待处理消息
   * @throws Exception 消费异常
   */
  protected abstract void handleMessage(T message) throws Exception;

  /**
   * 是否过滤消息，例如某些
   *
   * @param message 待处理消息
   * @return true: 本次消息被过滤，false：不过滤
   */
  protected boolean isFilter(T message) {
    return false;
  }

  /**
   * 由父类来完成基础的日志和调配，下面的只是提供一个思路
   */
  public void dispatchMessage(T message) {
    // 基础日志记录被父类处理了
    log.info("[{}]消费者收到消息[{}]", consumerName(), JSONUtil.toJsonStr(message));
    if (isFilter(message)) {
      log.info("消息不满足消费条件，已过滤");
      return;
    }

    try {
      long start = Instant.now().toEpochMilli();
      handleMessage(message);
      long end = Instant.now().toEpochMilli();
      log.info("消息消费成功，耗时[{}ms]", (end - start));
    } catch (Exception e) {
      log.error("[{}]消费者消息消费异常", consumerName(), e);
      //throw new MqException("消费者消息消费异常", MqExceptionTypeEnum.CONSUMER, e);
    }
  }
}
