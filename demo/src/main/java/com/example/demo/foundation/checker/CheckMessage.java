package com.example.demo.foundation.checker;

/**
 * Interface for check message enums
 * Implement this interface to create custom validation message enums
 */
public interface CheckMessage {
    
    /**
     * Get the message template with placeholders
     * Example: "Weather data already exists for city {city} on {date}"
     * 
     * @return the message template string
     */
    String getMessageTemplate();
}
