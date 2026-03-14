package com.example.demo.service.weather.arg;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWeatherArg {
    private String city;
    private LocalDate date;
    private Integer tempLo;
    private Integer tempHi;
    private Float prcp;
}
