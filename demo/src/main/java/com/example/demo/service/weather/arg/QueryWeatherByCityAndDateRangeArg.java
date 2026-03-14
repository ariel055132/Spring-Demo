package com.example.demo.service.weather.arg;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryWeatherByCityAndDateRangeArg {
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
}
