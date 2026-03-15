package com.example.demo.foundation.checker;

/**
 * Enum defining the type of validation check to perform
 */
public enum CheckType {
    /**
     * Check if data already exists (throw exception if duplicate found)
     * Used for CREATE operations
     */
    CREATE,
    
    /**
     * Check if data exists (throw exception if not found)
     * Used for UPDATE operations
     */
    UPDATE,
    
    /**
     * Check if data exists (throw exception if not found)
     * Used for DELETE operations
     */
    DELETE
}
