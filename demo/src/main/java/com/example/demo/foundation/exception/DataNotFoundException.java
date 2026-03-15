package com.example.demo.foundation.exception;

/**
 * Exception thrown when attempting to update/delete data that doesn't exist
 * Message should be provided by the user for better context
 */
public class DataNotFoundException extends RuntimeException {
    
    public DataNotFoundException(String message) {
        super(message);
    }
}
