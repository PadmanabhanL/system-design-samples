package com.sample.aws.dynamo.basic_crud.repository;

import com.sample.aws.dynamo.basic_crud.perf.MetricsContextHolder;

import java.util.concurrent.Callable;

public class JdbcListeners<T> {

    public static <T> T wrap(Callable<T> task) {
        Long startTime = System.currentTimeMillis();
        MetricsContextHolder.putContextVariable("sqlStartTime", startTime);
        try {
            return task.call();
        } catch (Exception ex) {
          return null;
        } finally {
            Object startObj = MetricsContextHolder.getContextVariable("sqlStartTime");
            if (startObj instanceof Long) {
                long sqlStart = (Long) startObj;
                MetricsContextHolder.putContextVariable("sqlDuration", System.currentTimeMillis() - sqlStart);
            }
        }
    }
}
