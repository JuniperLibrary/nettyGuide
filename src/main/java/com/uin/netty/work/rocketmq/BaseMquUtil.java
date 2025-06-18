package com.uin.netty.work.rocketmq;

import cn.hutool.core.util.*;
import com.fasterxml.jackson.databind.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import org.apache.rocketmq.common.message.*;

public class BaseMquUtil {

  private static final String class_key = "CLASS_NAME";

  public static HashMap<String, String> getMqHeader(Object o) {
    HashMap<String, String> header = new HashMap<>();
    if (ObjectUtil.isNotEmpty(o)) {
      String className = o.getClass().getName();
      header.put(class_key, className);
    }
    return header;
  }

  public static Object getMsg(MessageExt msg, ObjectMapper objectMapper)
      throws ClassNotFoundException, IOException {
    String messageBody = new String(msg.getBody(), StandardCharsets.UTF_8);
    Map<String, String> properties = msg.getProperties();
    Object o = null;
    if (properties.containsKey(class_key)) {
      String className = properties.get(class_key);
      Class<?> aClass = Class.forName(className);
      o = objectMapper.readValue(messageBody, aClass);
    }
    return o;
  }
}
