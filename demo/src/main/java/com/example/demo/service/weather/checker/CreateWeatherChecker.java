package com.example.demo.service.weather.checker;

import org.springframework.stereotype.Component;

import com.example.demo.service.weather.arg.CreateWeatherArg;

/**
 * Checker for Create Weather operation
 * Validates that weather data does not already exist before creation
 */
@Component
public class CreateWeatherChecker extends WeatherChecker<CreateWeatherArg> {
    
    @Override
    protected void doCheckInternal(CreateWeatherArg arg) {
        if (isWeatherExist(arg.getCity(), arg.getDate())) {
            String errorMessage = WeatherCheckMessageEnum.CREATE_DUPLICATE.getMessage(
                arg.getCity(), 
                arg.getDate().toString()
            );
            throw new IllegalStateException(errorMessage);
        }
    }
}
