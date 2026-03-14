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

    public UpdateWeatherArg toUpdateArg(UpdateWeatherRequest request) {
        return UpdateWeatherArg.builder()
                .id(request.getId())
                .city(request.getCity())
                .tempLo(request.getTempLo())
                .tempHi(request.getTempHi())
                .prcp(request.getPrcp())
                .date(request.getDate())
                .build();
    }

    public QueryWeatherByCityAndDateArg toQueryByCityAndDateArg(GetWeatherByCityAndDateRequest request) {
        return QueryWeatherByCityAndDateArg.builder()
                .city(request.getCity())
                .date(request.getDate())
                .build();
    }

    public QueryWeatherByCityAndDateRangeArg toQueryByCityAndDateRangeArg(GetWeatherByCityAndDateRangeRequest request) {
        return QueryWeatherByCityAndDateRangeArg.builder()
                .city(request.getCity())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
    }
}
