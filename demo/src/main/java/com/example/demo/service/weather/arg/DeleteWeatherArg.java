package com.example.demo.service.weather.arg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteWeatherArg {
    private String city;
    private LocalDate date;
}
