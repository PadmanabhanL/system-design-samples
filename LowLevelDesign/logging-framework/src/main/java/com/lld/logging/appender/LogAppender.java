package com.lld.logging.appender;

import com.lld.logging.LogMessage;

public interface LogAppender {
    void append(LogMessage logMessage);
}
