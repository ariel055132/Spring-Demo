package com.example.demo.service.weather.arg;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryWeatherByCityAndDateArg {
    private String city;
    private LocalDate date;
}
