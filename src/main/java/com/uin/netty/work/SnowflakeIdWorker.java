package com.uin.netty.work;

/**
 * @Author: Jin.HE
 * @Date: 2021/1/25 18:59
 */
public class SnowflakeIdWorker {

  // ==============================Fields===========================================
  /**
   * 开始时间截 (2022-01-01)
   */
  private static final long twepoch = 1640966400000L;

  /**
   * 机器id所占的位数
   */
  private static final long workerIdBits = 5L;

  /**
   * 数据标识id所占的位数
   */
  private static final long datacenterIdBits = 5L;

  /**
   * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
   */
  private static final long maxWorkerId = -1L ^ (-1L << workerIdBits);

  /**
   * 支持的最大数据标识id，结果是31
   */
  private static final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

  /**
   * 序列在id中占的位数
   */
  private static final long sequenceBits = 12L;

  /**
   * 机器ID向左移12位
   */
  private static final long workerIdShift = sequenceBits;

  /**
   * 数据标识id向左移17位(12+5)
   */
  private static final long datacenterIdShift = sequenceBits + workerIdBits;

  /**
   * 时间截向左移22位(5+5+12)
   */
  private static final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

  /**
   * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
   */
  private static final long sequenceMask = -1L ^ (-1L << sequenceBits);

  /**
   * 工作机器ID(0~31)
   */
  private static long workerId;

  /**
   * 数据中心ID(0~31)
   */
  private static long datacenterId;

  /**
   * 毫秒内序列(0~4095)
   */
  private static long sequence = 0L;

  /**
   * 上次生成ID的时间截
   */
  private static long lastTimestamp = -1L;

  //==============================Constructors=====================================

  private final long DEFAULT_WORKER_ID = 0;
  private final long DEFAULT_DATACENTER_ID = 0;

  /**
   * 构造函数
   */
  public SnowflakeIdWorker(long workerId) {
//    if (DEFAULT_WORKER_ID > maxWorkerId || workerId < 0) {
//      throw new IllegalArgumentException(String.format
//          ("worker Id can't be greater than %d or less than 0", maxWorkerId));
//    }
//    if (datacenterId > maxDatacenterId || datacenterId < 0) {
//      throw new IllegalArgumentException(String.format
//          ("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
//    }
    this.workerId = DEFAULT_WORKER_ID;
    this.datacenterId = DEFAULT_DATACENTER_ID;
  }

  // ==============================Methods==========================================

  /**
   * 获得下一个ID (该方法是线程安全的)
   *
   * @return SnowflakeId
   */
  public static synchronized long nextId() {
    long timestamp = timeGen();

    //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
    if (timestamp < lastTimestamp) {
      throw new RuntimeException(
          String.format
              ("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                  lastTimestamp - timestamp));
    }

    //如果是同一时间生成的，则进行毫秒内序列
    if (lastTimestamp == timestamp) {
      sequence = (sequence + 1) & sequenceMask;
      //毫秒内序列溢出
      if (sequence == 0) {
        //阻塞到下一个毫秒,获得新的时间戳
        timestamp = tilNextMillis(lastTimestamp);
      }
    }
    //时间戳改变，毫秒内序列重置
    else {
      sequence = 0L;
    }

    //上次生成ID的时间截
    lastTimestamp = timestamp;

    //移位并通过或运算拼到一起组成64位的ID
    return ((timestamp - twepoch) << timestampLeftShift) //
        | (datacenterId << datacenterIdShift) //
        | (workerId << workerIdShift) //
        | sequence;
  }

  /**
   * 阻塞到下一个毫秒，直到获得新的时间戳
   *
   * @param lastTimestamp 上次生成ID的时间截
   * @return 当前时间戳
   */
  protected static long tilNextMillis(long lastTimestamp) {
    long timestamp = timeGen();
    while (timestamp <= lastTimestamp) {
      timestamp = timeGen();
    }
    return timestamp;
  }

  /**
   * 返回以毫秒为单位的当前时间
   *
   * @return 当前时间(毫秒)
   */
  protected static long timeGen() {
    return System.currentTimeMillis();
  }

  public static void init(long workerId) {
    SnowflakeIdWorker.workerId = workerId;
  }
}
