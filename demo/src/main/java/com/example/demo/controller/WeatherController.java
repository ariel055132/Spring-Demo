package com.example.demo.controller;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.BaseResponse;
import com.example.demo.dto.response.WeatherResponse;
import com.example.demo.entity.Weather;
import com.example.demo.service.WeatherService;
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

    // Create
    @PostMapping("/create")
    @Operation(summary = "Create a new weather record")
    public BaseResponse<WeatherResponse> createWeather(@RequestBody CreateWeatherRequest request) {
        Weather weather = new Weather();
        weather.setCity(request.getCity());
        weather.setTempLo(request.getTempLo());
        weather.setTempHi(request.getTempHi());
        weather.setPrcp(request.getPrcp());
        weather.setDate(request.getDate());
        
        Weather savedWeather = weatherService.saveWeather(weather);
        WeatherResponse response = WeatherResponse.fromEntity(savedWeather);
        
        return BaseResponse.success("Weather record created successfully", response);
    }

    // Read - Get all
    @PostMapping("/getAll")
    @Operation(summary = "Get all weather records")
    public BaseResponse<List<WeatherResponse>> getAllWeather() {
        List<Weather> weatherList = weatherService.getAllWeather();
        List<WeatherResponse> responseList = weatherList.stream()
                .map(WeatherResponse::fromEntity)
                .collect(Collectors.toList());
        
        return BaseResponse.success(responseList);
    }

    // Read - Get by ID
    @PostMapping("/getById")
    @Operation(summary = "Get weather record by ID")
    public BaseResponse<WeatherResponse> getWeatherById(@RequestBody GetWeatherByIdRequest request) {
        return weatherService.getWeatherById(request.getId())
                .map(weather -> BaseResponse.success(WeatherResponse.fromEntity(weather)))
                .orElse(BaseResponse.error("Weather record not found with id: " + request.getId()));
    }

    // Read - Get by city
    @PostMapping("/getByCity")
    @Operation(summary = "Get weather records by city name")
    public BaseResponse<List<WeatherResponse>> getWeatherByCity(@RequestBody GetWeatherByCityRequest request) {
        List<Weather> weatherList = weatherService.getWeatherByCity(request.getCity());
        List<WeatherResponse> responseList = weatherList.stream()
                .map(WeatherResponse::fromEntity)
                .collect(Collectors.toList());
        
        return BaseResponse.success(responseList);
    }

    // Read - Get by city and date >= specified date
    @PostMapping("/getByCityAndDate")
    @Operation(summary = "Get weather records by city and date greater than or equal to specified date")
    public BaseResponse<List<WeatherResponse>> getWeatherByCityAndDate(@RequestBody GetWeatherByCityAndDateRequest request) {
        List<Weather> weatherList = weatherService.getWeatherByCityAndDate(request.getCity(), request.getDate());
        List<WeatherResponse> responseList = weatherList.stream()
                .map(WeatherResponse::fromEntity)
                .collect(Collectors.toList());
        
        return BaseResponse.success(responseList);
    }

    // Read - Get by city and date range
    @PostMapping("/getByCityAndDateRange")
    @Operation(summary = "Get weather records by city and date range")
    public BaseResponse<List<WeatherResponse>> getWeatherByCityAndDateRange(@RequestBody GetWeatherByCityAndDateRangeRequest request) {
        List<Weather> weatherList = weatherService.getWeatherByCityAndDateRange(
                request.getCity(), 
                request.getStartDate(), 
                request.getEndDate()
        );
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
            Weather weatherDetails = new Weather();
            weatherDetails.setCity(request.getCity());
            weatherDetails.setTempLo(request.getTempLo());
            weatherDetails.setTempHi(request.getTempHi());
            weatherDetails.setPrcp(request.getPrcp());
            weatherDetails.setDate(request.getDate());
            
            Weather updatedWeather = weatherService.updateWeather(request.getId(), weatherDetails);
            WeatherResponse response = WeatherResponse.fromEntity(updatedWeather);
            
            return BaseResponse.success("Weather record updated successfully", response);
        } catch (RuntimeException e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    // Delete by ID
    @PostMapping("/delete")
    @Operation(summary = "Delete a weather record by ID")
    public BaseResponse<Void> deleteWeather(@RequestBody DeleteWeatherRequest request) {
        try {
            weatherService.deleteWeather(request.getId());
            return BaseResponse.success("Weather record deleted successfully", null);
        } catch (Exception e) {
            return BaseResponse.error("Failed to delete weather record: " + e.getMessage());
        }
    }

    // Delete all
    @PostMapping("/deleteAll")
    @Operation(summary = "Delete all weather records")
    public BaseResponse<Void> deleteAllWeather() {
        try {
            weatherService.deleteAllWeather();
            return BaseResponse.success("All weather records deleted successfully", null);
        } catch (Exception e) {
            return BaseResponse.error("Failed to delete all weather records: " + e.getMessage());
        }
    }
}
