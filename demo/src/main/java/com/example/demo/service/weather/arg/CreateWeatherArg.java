package com.example.demo.service.weather.arg;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWeatherArg {
    private String city;
    private Integer tempLo;
    private Integer tempHi;
    private Float prcp;
    private LocalDate date;
}
