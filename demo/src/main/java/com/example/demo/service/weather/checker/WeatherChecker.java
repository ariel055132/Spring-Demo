package com.example.demo.service.weather.checker;

import com.example.demo.entity.Weather;
import com.example.demo.foundation.checker.BaseChecker;
import com.example.demo.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Base checker for Weather entity operations
 * Provides repository access for weather data validation
 * Extends BaseChecker
 */
@Component
public abstract class WeatherChecker<T> extends BaseChecker<T> {
    
    @Autowired
    protected WeatherRepository weatherRepository;

    /**
     * Check if weather data exists for given city and date
     * @param city City name
     * @param date Date
     * @return true if weather data exists, false otherwise
     */
    protected boolean isWeatherExist(String city, LocalDate date) {
        Weather weather = weatherRepository.findByCityAndDate(city, date);
        return weather != null;
    }
}
