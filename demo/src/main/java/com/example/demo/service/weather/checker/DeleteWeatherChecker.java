package com.example.demo.service.weather.checker;

import org.springframework.stereotype.Component;

import com.example.demo.service.weather.arg.DeleteWeatherArg;

/**
 * Checker for Delete Weather operation
 * Validates that weather data exists before deletion
 */
@Component
public class DeleteWeatherChecker extends WeatherChecker<DeleteWeatherArg> {
    
    @Override
    protected void doCheckInternal(DeleteWeatherArg arg) {
        if (!isWeatherExist(arg.getCity(), arg.getDate())) {
            String errorMessage = WeatherCheckMessageEnum.DELETE_NOT_FOUND.getMessage(
                arg.getCity(), 
                arg.getDate().toString()
            );
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
