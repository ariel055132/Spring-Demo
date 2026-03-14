package com.example.demo.dto.response;

import com.example.demo.entity.Weather;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    private Long id;
    private String city;
    private Integer tempLo;
    private Integer tempHi;
    private Float prcp;
    private LocalDate date;
    
    public static WeatherResponse fromEntity(Weather weather) {
        return new WeatherResponse(
            weather.getId(),
            weather.getCity(),
            weather.getTempLo(),
            weather.getTempHi(),
            weather.getPrcp(),
            weather.getDate()
        );
    }
}
