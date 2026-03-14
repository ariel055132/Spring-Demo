package com.example.demo.controller.weather.converter;

import com.example.demo.controller.weather.dto.*;
import com.example.demo.service.weather.arg.*;
import org.springframework.stereotype.Component;

@Component
public class WeatherRequestConverter {

    public CreateWeatherArg toCreateArg(CreateWeatherRequest request) {
        return CreateWeatherArg.builder()
                .city(request.getCity())
                .tempLo(request.getTempLo())
                .tempHi(request.getTempHi())
                .prcp(request.getPrcp())
                .date(request.getDate())
                .build();
    }

    public ReadWeatherArg toReadArg(ReadWeatherRequest request) {
        return ReadWeatherArg.builder()
                .city(request.getCity())
                .date(request.getDate())
                .build();
    }

    public UpdateWeatherArg toUpdateArg(UpdateWeatherRequest request) {
        return UpdateWeatherArg.builder()
                .city(request.getCity())
                .date(request.getDate())
                .tempLo(request.getTempLo())
                .tempHi(request.getTempHi())
                .prcp(request.getPrcp())
                .build();
    }

    public DeleteWeatherArg toDeleteArg(DeleteWeatherRequest request) {
        return DeleteWeatherArg.builder()
                .city(request.getCity())
                .date(request.getDate())
                .build();
    }
}
