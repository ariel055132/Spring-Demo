package com.example.foundation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple utility class for logging operations
 * Automatically detects the calling class and creates appropriate logger
 */
public class LogUtil {

    private static final String LOG_UTIL_CLASS_NAME = LogUtil.class.getName();

    /**
     * Get logger for the calling class.
     * Scans the stack for the first frame outside LogUtil so that AOP proxies
     * or additional wrapper layers do not shift the hardcoded offset and cause
     * log entries to be attributed to the wrong class.
     */
    private static Logger getLogger() {
        for (StackTraceElement frame : Thread.currentThread().getStackTrace()) {
            String className = frame.getClassName();
            // Skip JVM internals and LogUtil itself
            if (!className.startsWith("java.") && !className.equals(LOG_UTIL_CLASS_NAME)) {
                return LoggerFactory.getLogger(className);
            }
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

