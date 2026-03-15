package com.example.demo.foundation.exception;

/**
 * Exception thrown when attempting to create data that already exists
 * Message should be provided by the user for better context
 */
public class DuplicateDataException extends RuntimeException {
    
    public DuplicateDataException(String message) {
        super(message);
    }
}
