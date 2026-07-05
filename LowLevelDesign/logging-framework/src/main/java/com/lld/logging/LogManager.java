package com.lld.logging;

import com.lld.logging.appender.ConsoleAppender;
import com.lld.logging.appender.LogAppender;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogManager {
    private static volatile LogManager instance;
    private final Map<String, Logger> loggers;
    private final LogAppender defaultAppender;

    private LogManager() {
        loggers = new ConcurrentHashMap<>();
        defaultAppender = new ConsoleAppender();
    }

    public static LogManager getInstance() {
        if (instance == null) {
            synchronized (LogManager.class) {
                if (instance == null) {
                    instance = new LogManager();
                }
            }
        }
        return instance;
    }

    public Logger getLogger(String name) {
        return loggers.computeIfAbsent(name, k -> {
            Logger logger = new Logger(name);
            logger.addAppender(defaultAppender);
            return logger;
        });
    }

    public Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }
}
