package com.uin.netty.work.rocketmq;

/**
 * RocketMQ延迟等级
 */
public enum RocketMqDelayLevelEnum {

  IMMEDIATE(-1),
  ONE_SECOND(1),
  FIVE_SECOND(2),
  TEN_SECOND(3),
  THIRTY_SECOND(4),
  ONE_MINUTE(5),
  TWO_MINUTE(6),
  THREE_MINUTE(7),
  FOUR_MINUTE(8),
  FIVE_MINUTE(9),
  SIX_MINUTE(10),
  SEVEN_MINUTE(11),
  EIGHT_MINUTE(12),
  NINE_MINUTE(13),
  TEN_MINUTE(14),
  TWENTY_MINUTE(15),
  THIRTY_MINUTE(16),
  ONE_HOUR(17),
  TWO_HOUR(18);

  /**
   * 延迟级别
   */
  private int level;

  RocketMqDelayLevelEnum(int level) {
    this.level = level;
  }

  public int getLevel() {
    return level;
  }
}
