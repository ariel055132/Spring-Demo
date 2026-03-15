package com.example.demo.service.weather.checker;

import com.example.demo.entity.Weather;
import com.example.demo.foundation.exception.DataNotFoundException;
import com.example.demo.foundation.exception.DuplicateDataException;
import com.example.demo.repository.WeatherRepository;
import com.example.demo.service.weather.arg.CreateWeatherArg;
import com.example.demo.service.weather.arg.DeleteWeatherArg;
import com.example.demo.service.weather.arg.UpdateWeatherArg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Specific checker for Weather entity operations
 * Validates data existence and duplication with custom error messages
 */
@Component
public class WeatherChecker {
    
    @Autowired
    private WeatherRepository weatherRepository;
    
    /**
     * Check for duplicate weather data before creating
     * Throws DuplicateDataException if weather record already exists for the city and date
     * 
     * @param arg CreateWeatherArg containing city and date
     * @param customMessage Custom error message to throw if duplicate is found
     * @throws DuplicateDataException if duplicate weather data exists
     */
    public void CreateWeatherChecker(CreateWeatherArg arg, String customMessage) {
        List<Weather> existingWeather = weatherRepository.findByCityAndDate(arg.getCity(), arg.getDate());
        
        if (!existingWeather.isEmpty()) {
            throw new DuplicateDataException(customMessage);
        }
    }
    
    /**
     * Check if weather data exists before updating
     * Throws DataNotFoundException if weather record doesn't exist for the city and date
     * 
     * @param arg UpdateWeatherArg containing city and date
     * @param customMessage Custom error message to throw if data is not found
     * @throws DataNotFoundException if weather data doesn't exist
     */
    public void UpdateWeatherChecker(UpdateWeatherArg arg, String customMessage) {
        List<Weather> existingWeather = weatherRepository.findByCityAndDate(arg.getCity(), arg.getDate());
        
        if (existingWeather.isEmpty()) {
            throw new DataNotFoundException(customMessage);
        }
    }
    
    /**
     * Check if weather data exists before deleting
     * Throws DataNotFoundException if weather record doesn't exist for the city and date
     * 
     * @param arg DeleteWeatherArg containing city and date
     * @param customMessage Custom error message to throw if data is not found
     * @throws DataNotFoundException if weather data doesn't exist
     */
    public void DeleteWeatherChecker(DeleteWeatherArg arg, String customMessage) {
        List<Weather> existingWeather = weatherRepository.findByCityAndDate(arg.getCity(), arg.getDate());
        
        if (existingWeather.isEmpty()) {
            throw new DataNotFoundException(customMessage);
        }
    }
}
