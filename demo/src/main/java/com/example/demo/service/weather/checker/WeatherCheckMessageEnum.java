package com.example.demo.service.weather.checker;

import com.example.demo.foundation.checker.CheckMessage;

/**
 * Centralized error messages for Weather validation
 * Supports placeholders: {city}, {date} that will be replaced with actual values
 */
public enum WeatherCheckMessageEnum implements CheckMessage {
    
    CREATE_DUPLICATE("Weather data already exists for {city} on {date}"),
    UPDATE_NOT_FOUND("No weather data found for {city} on {date}. Unable to update."),
    DELETE_NOT_FOUND("No weather data found for {city} on {date}. Unable to delete.");
    
    private final String messageTemplate;
    
    WeatherCheckMessageEnum(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }
    
    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }
    
    /**
     * Get message with placeholders replaced
     */
    public String getMessage(String city, String date) {
        return messageTemplate
                .replace("{city}", city != null ? city : "null")
                .replace("{date}", date != null ? date : "null");
    }
}
