package com.example.foundation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple utility class for logging operations
 * Automatically detects the calling class and creates appropriate logger
 */
public class LogUtil {

    /**
     * Get logger for the calling class
     */
    private static Logger getLogger() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // stackTrace[0] is getStackTrace
        // stackTrace[1] is getLogger
        // stackTrace[2] is the LogUtil method (addInfo, wrongInfo, debugInfo)
        // stackTrace[3] is the actual caller
        if (stackTrace.length > 3) {
            String callerClassName = stackTrace[3].getClassName();
            return LoggerFactory.getLogger(callerClassName);
        }
        return LoggerFactory.getLogger(LogUtil.class);
    }

    /**
     * Log an informational message
     * @param message the message to log
     */
    public static void addInfo(String message) {
        getLogger().info(message);
    }

    /**
     * Log an informational message with parameters
     * @param message the message format
     * @param args the message arguments
     */
    public static void addInfo(String message, Object... args) {
        getLogger().info(message, args);
    }

    /**
     * Log a warning/error message
     * @param message the message to log
     */
    public static void wrongInfo(String message) {
        getLogger().error(message);
    }

    /**
     * Log a warning/error message with parameters
     * @param message the message format
     * @param args the message arguments
     */
    public static void wrongInfo(String message, Object... args) {
        getLogger().error(message, args);
    }

    /**
     * Log a warning/error message with exception
     * @param message the message to log
     * @param throwable the exception
     */
    public static void wrongInfo(String message, Throwable throwable) {
        getLogger().error(message, throwable);
    }

    /**
     * Log a debug message
     * @param message the message to log
     */
    public static void debugInfo(String message) {
        getLogger().debug(message);
    }

    /**
     * Log a debug message with parameters
     * @param message the message format
     * @param args the message arguments
     */
    public static void debugInfo(String message, Object... args) {
        getLogger().debug(message, args);
    }
}

