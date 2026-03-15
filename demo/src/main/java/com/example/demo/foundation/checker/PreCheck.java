package com.example.demo.foundation.checker;

import com.example.demo.service.weather.checker.WeatherCheckMessageEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable pre-check validation before executing business logic.
 * Used to check for duplicate data (CREATE) or missing data (UPDATE/DELETE).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreCheck {
    /**
     * Type of operation to validate (CREATE, UPDATE, or DELETE)
     */
    CheckType value();
    
    /**
     * Error message enum to use if validation fails.
     * The message can contain placeholders like {city}, {date} that will be replaced.
     */
    WeatherCheckMessageEnum message();
}
