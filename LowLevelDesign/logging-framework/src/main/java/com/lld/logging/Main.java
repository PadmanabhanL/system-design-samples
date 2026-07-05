package com.lld.logging;

import com.lld.logging.appender.ConsoleAppender;

public class Main {
    public static void main(String[] args) {
        LogManager logManager = LogManager.getInstance();
        
        Logger logger = logManager.getLogger(Main.class);
        
        System.out.println("--- Default Logging (INFO) ---");
        logger.debug("This is a debug message - should not print");
        logger.info("This is an info message");
        logger.warn("This is a warning message");
        logger.error("This is an error message");
        
        System.out.println("\n--- Changing Log Level to DEBUG ---");
        logger.setLevel(LogLevel.DEBUG);
        logger.debug("This is a debug message - should print now");
        
        System.out.println("\n--- Creating another logger ---");
        Logger appLogger = logManager.getLogger("AppLogger");
        appLogger.setLevel(LogLevel.FATAL);
        appLogger.error("This error should not print because level is FATAL");
        appLogger.fatal("This is a fatal error message!");
    }
}
