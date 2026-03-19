package com.example.demo.service.weather.checker;

import org.springframework.stereotype.Component;

import com.example.demo.service.weather.arg.UpdateWeatherArg;

/**
 * Checker for Update Weather operation
 * Validates that weather data exists before updating
 */
@Component
public class UpdateWeatherChecker extends WeatherChecker<UpdateWeatherArg> {
    
    @Override
    protected void doCheckInternal(UpdateWeatherArg arg) {
        if (!isWeatherExist(arg.getCity(), arg.getDate())) {
            String errorMessage = WeatherCheckMessageEnum.UPDATE_NOT_FOUND.getMessage(
                arg.getCity(), 
                arg.getDate().toString()
            );
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
