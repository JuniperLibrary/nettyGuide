package com.uin.netty;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.SlidingTimeWindowReservoir;
import com.codahale.metrics.Timer;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Author:
 * @Date:
 */
@Slf4j
public class MetricsMarker {

  private static boolean enable = true;

  private static final MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate("Metric");

  private static final long default_time = 300;//默认时间段为300秒

  /**
   * Metrics.Gauge埋点
   *
   * @param module
   * @param topic
   * @param gauge
   */
  public static void setGauge(String module, String topic, Gauge gauge) {
    if (!enable) {
      return;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.GAUGE_MEASUREMENTS, module, topic);
    if (metricRegistry.getMetrics().get(metricsKey) == null) {
      metricRegistry.register(metricsKey, gauge);
    } else {
      log.warn("A metric named {} already exists", metricsKey);
    }
  }

  public static void setGauge(String module, String topic, Gauge gauge,
      ReporterType... reporterTypes) {
    if (!enable) {
      return;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.GAUGE_MEASUREMENTS, module, topic,
        analysisReporterType(reporterTypes));
    if (metricRegistry.getMetrics().get(metricsKey) == null) {
      metricRegistry.register(metricsKey, gauge);
    } else {
      log.warn("A metric named {} already exists", metricsKey);
    }
  }

  /**
   * Metrics.Counter(inc)埋点
   *
   * @param module
   * @param topic
   * @param count
   */
  public static void incCounter(String module, String topic, long count) {
    if (!enable) {
      return;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.COUNTER_MEASUREMENTS, module, topic);
    metricRegistry.counter(metricsKey).inc(count);
  }

  public static void incCounter(String module, String topic, long count,
      ReporterType... reporterTypes) {
    if (!enable) {
      return;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.COUNTER_MEASUREMENTS, module, topic,
        analysisReporterType(reporterTypes));
    metricRegistry.counter(metricsKey).inc(count);
  }

  /**
   * Metrics.Counter(dec)埋点
   *
   * @param module
   * @param topic
   * @param count
   */
  public static void decCounter(String module, String topic, long count) {
    if (!enable) {
      return;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.COUNTER_MEASUREMENTS, module, topic);
    metricRegistry.counter(metricsKey).dec(count);
  }

  public static void decCounter(String module, String topic, long count,
      ReporterType... reporterTypes) {
    if (!enable) {
      return;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.COUNTER_MEASUREMENTS, module, topic,
        analysisReporterType(reporterTypes));
    metricRegistry.counter(metricsKey).dec(count);
  }

  /**
   * Metrics.Counter()重置counter
   *
   * @param module
   * @param topic
   */
  public static void resetCounter(String module, String topic) {
    if (!enable) {
      return;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.COUNTER_MEASUREMENTS, module, topic);
    metricRegistry.remove(metricsKey);
  }

  public static void resetCounter(String module, String topic, ReporterType... reporterTypes) {
    if (!enable) {
      return;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.COUNTER_MEASUREMENTS, module, topic,
        analysisReporterType(reporterTypes));
    metricRegistry.remove(metricsKey);
  }

  /**
   * Metrics.Meter(count)埋点
   *
   * @param module
   * @param topic
   * @param count
   */
  public static void markMeter(String module, String topic, long count) {
    if (!enable) {
      return;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.METER_MEASUREMENTS, module, topic);
    metricRegistry.meter(metricsKey).mark(count);
  }

  public static void markMeter(String module, String topic, long count,
      ReporterType... reporterTypes) {
    if (!enable) {
      return;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.METER_MEASUREMENTS, module, topic,
        analysisReporterType(reporterTypes));
    metricRegistry.meter(metricsKey).mark(count);
  }

  /**
   * Metrics.Timer()埋点
   *
   * @param module
   * @param topic
   * @return
   */
  public static Timer.Context startTimer(String module, String topic) {
    if (!enable) {
      return null;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.TIMER_MEASUREMENTS, module, topic);
    return metricRegistry.timer(metricsKey).time();
  }

  public static Timer.Context startTimer(String module, String topic,
      ReporterType... reporterTypes) {
    if (!enable) {
      return null;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.TIMER_MEASUREMENTS, module, topic,
        analysisReporterType(reporterTypes));
    return metricRegistry.timer(metricsKey).time();
  }

  /***
   * 重置timer
   * @param module
   * @param topic
   */
  public static void resetTimer(String module, String topic) {
    String metricsKey = MetricRegistry.name(MetricsConstant.TIMER_MEASUREMENTS, module, topic);
    metricRegistry.remove(metricsKey);
  }

  public static void resetTimer(String module, String topic, ReporterType... reporterTypes) {
    String metricsKey = MetricRegistry.name(MetricsConstant.TIMER_MEASUREMENTS, module, topic,
        analysisReporterType(reporterTypes));
    metricRegistry.remove(metricsKey);
  }

  /**
   * Metrics.Timer()埋点，按时间段计算平均耗时
   *
   * @param module
   * @param topic
   * @param windowUnit 时间段单位
   * @return
   * @Param window 统计时间段
   */
  public static Timer.Context startTimerWithWindow(String module, String topic, long window,
      TimeUnit windowUnit) {
    if (!enable) {
      return null;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.TIMER_MEASUREMENTS, module, topic);
    SlidingTimeWindowReservoir reservoir = new SlidingTimeWindowReservoir(window, windowUnit);
    Timer t = new Timer(reservoir);
    if (Objects.isNull(metricRegistry.getMetrics().get(metricsKey))) {
      return metricRegistry.register(metricsKey, t).time();
    } else {
      return ((Timer) metricRegistry.getMetrics().get(metricsKey)).time();
    }
  }

  public static Timer.Context startTimerWithWindow(String module, String topic, long window,
      TimeUnit windowUnit, ReporterType... reporterTypes) {
    if (!enable) {
      return null;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.TIMER_MEASUREMENTS, module, topic,
        analysisReporterType(reporterTypes));
    SlidingTimeWindowReservoir reservoir = new SlidingTimeWindowReservoir(window, windowUnit);
    Timer t = new Timer(reservoir);
    if (Objects.isNull(metricRegistry.getMetrics().get(metricsKey))) {
      return metricRegistry.register(metricsKey, t).time();
    } else {
      return ((Timer) metricRegistry.getMetrics().get(metricsKey)).time();
    }
  }

  /**
   * Metrics.Timer()埋点，每300秒计算平均耗时
   *
   * @param module
   * @param topic
   * @return
   */
  public static Timer.Context startTimerWithWindow(String module, String topic) {
    if (!enable) {
      return null;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.TIMER_MEASUREMENTS, module, topic);
    SlidingTimeWindowReservoir reservoir = new SlidingTimeWindowReservoir(default_time,
        TimeUnit.SECONDS);
    Timer t = new Timer(reservoir);
    if (Objects.isNull(metricRegistry.getMetrics().get(metricsKey))) {
      return metricRegistry.register(metricsKey, t).time();
    } else {
      return ((Timer) metricRegistry.getMetrics().get(metricsKey)).time();
    }
  }

  public static Timer.Context startTimerWithWindow(String module, String topic,
      ReporterType... reporterTypes) {
    if (!enable) {
      return null;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.TIMER_MEASUREMENTS, module, topic,
        analysisReporterType(reporterTypes));
    SlidingTimeWindowReservoir reservoir = new SlidingTimeWindowReservoir(default_time,
        TimeUnit.SECONDS);
    Timer t = new Timer(reservoir);
    if (Objects.isNull(metricRegistry.getMetrics().get(metricsKey))) {
      return metricRegistry.register(metricsKey, t).time();
    } else {
      return ((Timer) metricRegistry.getMetrics().get(metricsKey)).time();
    }
  }

  /**
   * Metrics.Timer()埋点
   *
   * @param module
   * @param topic
   * @param duration
   * @param timeUnit
   */
  public static void updateTimer(String module, String topic, long duration, TimeUnit timeUnit) {
    if (!enable) {
      return;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.TIMER_MEASUREMENTS, module, topic);
    metricRegistry.timer(metricsKey).update(duration, timeUnit);
  }

  public static void updateTimer(String module, String topic, long duration, TimeUnit timeUnit,
      ReporterType... reporterTypes) {
    if (!enable) {
      return;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.TIMER_MEASUREMENTS, module, topic,
        analysisReporterType(reporterTypes));
    metricRegistry.timer(metricsKey).update(duration, timeUnit);
  }

  /**
   * Metrics.Timer()埋点 ，每300秒计算平均耗时
   *
   * @param module
   * @param topic
   * @param duration
   * @param timeUnit
   */
  public static void updateTimerWithWindow(String module, String topic, long duration,
      TimeUnit timeUnit) {
    if (!enable) {
      return;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.TIMER_MEASUREMENTS, module, topic);
    SlidingTimeWindowReservoir reservoir = new SlidingTimeWindowReservoir(default_time,
        TimeUnit.SECONDS);
    Timer t = new Timer(reservoir);
    if (Objects.isNull(metricRegistry.getMetrics().get(metricsKey))) {
      metricRegistry.register(metricsKey, t).update(duration, timeUnit);
    } else {
      ((Timer) metricRegistry.getMetrics().get(metricsKey)).update(duration, timeUnit);
    }
  }

  public static void updateTimerWithWindow(String module, String topic, long duration,
      TimeUnit timeUnit, ReporterType... reporterTypes) {
    if (!enable) {
      return;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.TIMER_MEASUREMENTS, module, topic,
        analysisReporterType(reporterTypes));
    SlidingTimeWindowReservoir reservoir = new SlidingTimeWindowReservoir(default_time,
        TimeUnit.SECONDS);
    Timer t = new Timer(reservoir);
    if (Objects.isNull(metricRegistry.getMetrics().get(metricsKey))) {
      metricRegistry.register(metricsKey, t).update(duration, timeUnit);
    } else {
      ((Timer) metricRegistry.getMetrics().get(metricsKey)).update(duration, timeUnit);
    }
  }

  /**
   * Metrics.Timer()埋点,按时间段计算平均耗时
   *
   * @param module
   * @param topic
   * @param duration
   * @param timeUnit
   */
  public static void updateTimerWithWindow(String module, String topic, long duration, long window,
      TimeUnit timeUnit) {
    if (!enable) {
      return;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.TIMER_MEASUREMENTS, module, topic);
    SlidingTimeWindowReservoir reservoir = new SlidingTimeWindowReservoir(window, timeUnit);
    Timer t = new Timer(reservoir);
    if (Objects.isNull(metricRegistry.getMetrics().get(metricsKey))) {
      metricRegistry.register(metricsKey, t).update(duration, timeUnit);
    } else {
      ((Timer) metricRegistry.getMetrics().get(metricsKey)).update(duration, timeUnit);
    }
  }

  public static void updateTimerWithWindow(String module, String topic, long duration, long window,
      TimeUnit timeUnit, ReporterType... reporterTypes) {
    if (!enable) {
      return;
    }
    String metricsKey = MetricRegistry.name(MetricsConstant.TIMER_MEASUREMENTS, module, topic,
        analysisReporterType(reporterTypes));
    SlidingTimeWindowReservoir reservoir = new SlidingTimeWindowReservoir(window, timeUnit);
    Timer t = new Timer(reservoir);
    if (Objects.isNull(metricRegistry.getMetrics().get(metricsKey))) {
      metricRegistry.register(metricsKey, t).update(duration, timeUnit);
    } else {
      ((Timer) metricRegistry.getMetrics().get(metricsKey)).update(duration, timeUnit);
    }
  }

  /**
   * 解析 reporter type ,用以区分向哪种reporter写入数据.
   *
   * @param reporterTypes
   * @return
   */
  private static String analysisReporterType(ReporterType... reporterTypes) {
    StringBuilder stringBuilder = new StringBuilder();
    for (ReporterType reporterType : reporterTypes) {
      stringBuilder.append(reporterType).append(":");
    }
    if (stringBuilder.length() > 0) {
      stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
    }
    return stringBuilder.toString();
  }

  public static void disable() {
    enable = false;
  }

  public static void enable() {
    enable = true;
  }

}
