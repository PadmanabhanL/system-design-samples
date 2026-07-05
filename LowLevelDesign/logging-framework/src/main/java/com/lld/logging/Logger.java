package com.lld.logging;

import com.lld.logging.appender.LogAppender;

import java.util.ArrayList;
import java.util.List;

public class Logger {
    private final String name;
    private LogLevel level;
    private final List<LogAppender> appenders;

    protected Logger(String name) {
        this.name = name;
        this.level = LogLevel.INFO; // default level
        this.appenders = new ArrayList<>();
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public void addAppender(LogAppender appender) {
        this.appenders.add(appender);
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public void fatal(String message) {
        log(LogLevel.FATAL, message);
    }

    private void log(LogLevel logLevel, String message) {
        if (logLevel.getLevel() >= this.level.getLevel()) {
            LogMessage logMessage = new LogMessage(logLevel, message);
            for (LogAppender appender : appenders) {
                appender.append(logMessage);
            }
        }
    }
}
