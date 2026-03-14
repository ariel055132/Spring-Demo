package com.example.demo.controller.weather.dto;

import com.example.demo.util.api.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeleteWeatherRequest extends BaseRequest {
    private Long id;
}
