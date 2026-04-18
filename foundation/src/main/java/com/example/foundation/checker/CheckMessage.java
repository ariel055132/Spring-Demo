package com.example.foundation.checker;

import java.util.Map;

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

    /**
     * Fill the template placeholders with the provided values.
     * Centralised here so callers never have to re-implement substitution logic.
     * Example: format(Map.of("city", "Taipei", "date", "2026-04-19"))
     *
     * @param params key-value pairs matching the {key} placeholders in the template
     * @return the resolved message string
     */
    default String format(Map<String, String> params) {
        String result = getMessageTemplate();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}
