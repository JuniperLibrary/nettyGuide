package com.uin.netty.protocoltcp;

/**
 * 自定义协议类
 */
public class MessageProtocol {

  /**
   * 协议合法性校验
   */
  private int magic = 0x12345678; // 魔数
  /**
   * 后续数据体的长度
   */
  private int length;
  /**
   * 业务数据，长度由 length 决定
   */
  private byte[] content;

  // 构造函数
  public MessageProtocol(byte[] content) {
    this.length = content.length;
    this.content = content;
  }

  // Getter & Setter
  public int getMagic() {
    return magic;
  }

  public int getLength() {
    return length;
  }

  public byte[] getContent() {
    return content;
  }
}
