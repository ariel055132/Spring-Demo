package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateWeatherRequest extends BaseRequest {
    @Schema(description = "城市",  example = "Hong Kong")
    @NotBlank
    private String city;

    
    private Integer tempLo;
    private Integer tempHi;
    private Float prcp;
    private LocalDate date;
}
