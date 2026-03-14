package com.example.demo.controller.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.example.demo.foundation.api.BaseRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReadWeatherRequest extends BaseRequest {
    private String city;
    private LocalDate date;
}
