package com.example.demo.controller.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateWeatherRequest extends BaseRequest {
    @Schema(description = "城市", example = "Hong Kong")
    @NotBlank
    private String city;
    
    @Schema(description = "最低溫度", example = "11")
    @NotNull
    private Integer tempLo;

    @Schema(description = "最高溫度", example = "12")
    @NotNull
    private Integer tempHi;
    private Float prcp;
    private LocalDate date;
}
