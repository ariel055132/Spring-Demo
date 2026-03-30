package com.example.foundation.checker;

/**
 * Interface for handling pre-check validation logic
 * Implement this interface to create custom validation handlers
 */
public interface PreCheckHandler<T> {
    
    /**
     * Perform validation check
     * Throws appropriate exception if validation fails
     * 
     * @param arg The argument object containing data to validate
     */
    void doCheck(Object arg);
}
