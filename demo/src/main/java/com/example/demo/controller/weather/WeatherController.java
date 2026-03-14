package com.example.demo.controller.weather;

import com.example.demo.controller.weather.converter.WeatherRequestConverter;
import com.example.demo.controller.weather.dto.CreateWeatherRequest;
import com.example.demo.controller.weather.dto.DeleteWeatherRequest;
import com.example.demo.controller.weather.dto.ReadWeatherRequest;
import com.example.demo.controller.weather.dto.UpdateWeatherRequest;
import com.example.demo.entity.Weather;
import com.example.demo.service.weather.WeatherService;
import com.example.demo.service.weather.arg.CreateWeatherArg;
import com.example.demo.service.weather.arg.DeleteWeatherArg;
import com.example.demo.service.weather.arg.ReadWeatherArg;
import com.example.demo.service.weather.arg.UpdateWeatherArg;
import com.example.demo.service.weather.response.WeatherResponse;
import com.example.demo.util.api.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/weather")
@Tag(name = "Weather", description = "Weather data management APIs")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;
    
    @Autowired
    private WeatherRequestConverter converter;

    // Create
    @PostMapping("/create")
    @Operation(summary = "Create a new weather record")
    public BaseResponse<WeatherResponse> createWeather(@RequestBody CreateWeatherRequest request) {
        CreateWeatherArg arg = converter.toCreateArg(request);
        Weather savedWeather = weatherService.createWeather(arg);
        WeatherResponse response = WeatherResponse.fromEntity(savedWeather);
        return BaseResponse.success("Weather record created successfully", response);
    }

    // Read
    @PostMapping("/read")
    @Operation(summary = "Get weather records by city and date")
    public BaseResponse<List<WeatherResponse>> readWeather(@RequestBody ReadWeatherRequest request) {
        ReadWeatherArg arg = converter.toReadArg(request);
        List<Weather> weatherList = weatherService.readWeather(arg);
        
        if (weatherList.isEmpty()) {
            return BaseResponse.error("No weather records found for city: " + request.getCity() + " on date: " + request.getDate());
        }
        
        List<WeatherResponse> responseList = weatherList.stream()
                .map(WeatherResponse::fromEntity)
                .collect(Collectors.toList());
        return BaseResponse.success(responseList);
    }

    // Update
    @PostMapping("/update")
    @Operation(summary = "Update an existing weather record")
    public BaseResponse<WeatherResponse> updateWeather(@RequestBody UpdateWeatherRequest request) {
        try {
            UpdateWeatherArg arg = converter.toUpdateArg(request);
            Weather updatedWeather = weatherService.updateWeather(arg);
            WeatherResponse response = WeatherResponse.fromEntity(updatedWeather);
            return BaseResponse.success("Weather record updated successfully", response);
        } catch (RuntimeException e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    // Delete
    @PostMapping("/delete")
    @Operation(summary = "Delete weather records by city and date")
    public BaseResponse<Void> deleteWeather(@RequestBody DeleteWeatherRequest request) {
        try {
            DeleteWeatherArg arg = converter.toDeleteArg(request);
            weatherService.deleteWeatherByCityAndDate(arg);
            return BaseResponse.success("Weather record(s) deleted successfully for city: " + request.getCity() + " on date: " + request.getDate(), null);
        } catch (Exception e) {
            return BaseResponse.error("Failed to delete weather record: " + e.getMessage());
        }
    }
}
