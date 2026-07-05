package com.lld.logging.appender;

import com.lld.logging.LogMessage;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ConsoleAppender implements LogAppender {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .withZone(ZoneId.systemDefault());

    @Override
    public void append(LogMessage logMessage) {
        String timestamp = formatter.format(Instant.ofEpochMilli(logMessage.getTimestamp()));
        String formattedMessage = String.format("[%s] [%s] [%s] - %s",
                timestamp,
                logMessage.getThreadName(),
                logMessage.getLevel().name(),
                logMessage.getMessage()
        );
        System.out.println(formattedMessage);
    }
}
