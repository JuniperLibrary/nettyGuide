package com.uin.netty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Author: Jin.HE
 * @Date: 2021/4/13 21:37
 */
@Slf4j
@Component
public class IdGenerator {

  public IdGenerator(@Value("${rates.serviceId:#{'0'}}") String serviceId) {
    log.info("IdGenerator init serviceId={}", serviceId);
    long workerId = 0;
    if (!StringUtils.isEmpty(serviceId)) {
      try {
        String serviceNo = getNumericStr(serviceId);
        if (!StringUtils.isEmpty(serviceNo) && isNumeric(serviceNo)) {
          workerId = Long.parseLong(serviceNo);
        }
      } catch (Exception e) {
        log.error("IdGenerator init error! serviceId={}, ", serviceId, e);
      }
    }
    SnowflakeIdWorker.init(workerId);
  }


  public static long nextId() {
    return SnowflakeIdWorker.nextId();
  }

  public static String getNumericStr(String serviceId) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < serviceId.length(); i++) {
      if (!Character.isDigit(serviceId.charAt(i))) {
        continue;
      }
      sb.append(serviceId.charAt(i));
    }
    return sb.toString();
  }

  public static boolean isNumeric(String str) {
    if (StringUtils.isEmpty(str)) {
      return false;
    }
    for (int i = str.length(); --i >= 0; ) {
      if (!Character.isDigit(str.charAt(i))) {
        return false;
      }
    }
    return true;
  }

}
