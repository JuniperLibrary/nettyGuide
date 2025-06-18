package com.uin.netty.work.rocketmq;

import java.io.*;
import java.time.*;
import java.util.*;
import lombok.*;

/**
 * 基础消息实体，包含基础的消息
 * 根据自己的业务消息设置更多的字段
 *
 * @author lijin
 */
@Data
public class BaseMqMessage implements Serializable {
    /**
     * 业务键，用于RocketMQ控制台查看消费情况
     */
    protected String key;
    /**
     * 发送消息来源，用于排查问题
     */
    protected MessageSource mqSource;
    /**
     * 发送时间
     */
    protected LocalDateTime dispatchTime = LocalDateTime.now();
    /**
     * 跟踪id，用于slf4j等日志记录跟踪id，方便查询业务链
     */
    protected String traceUuId = UUID.randomUUID().toString();

    //是否内部处理
    private boolean innerHandle=true;

}
