package com.sample.aws.dynamo.basic_crud.perf;

import java.util.HashMap;
import java.util.Map;

public class MetricsContextHolder {

    private static final ThreadLocal<MetricsContextHolder> metricsContextThreadLocal = new ThreadLocal<>();

    private final Map<String, Object> context = new HashMap<>();

    private MetricsContextHolder() {
    }

    public static void putContextVariable(String key, Object value) {
        if (metricsContextThreadLocal.get() == null) {
            metricsContextThreadLocal.set(new MetricsContextHolder());
        }
        MetricsContextHolder metricsContextHolder = metricsContextThreadLocal.get();
        if (metricsContextHolder != null) {
            metricsContextHolder.context.put(key, value);
        }
    }

    public static void removeContextVariable(String key) {
        if (metricsContextThreadLocal.get() == null) {
            return;
        }
        MetricsContextHolder metricsContextHolder = metricsContextThreadLocal.get();
        if (metricsContextHolder != null) {
            metricsContextHolder.context.remove(key);
        }
    }

    public static Object getContextVariable(String key) {
        if (metricsContextThreadLocal.get() == null) {
            return null;
        }
        MetricsContextHolder metricsContextHolder = metricsContextThreadLocal.get();
        return metricsContextHolder.context.get(key);
    }

    public static void clearContextVariables() {
        if (metricsContextThreadLocal.get() == null) {
            return;
        }
        metricsContextThreadLocal.get().context.clear();
        metricsContextThreadLocal.remove();
    }

}
