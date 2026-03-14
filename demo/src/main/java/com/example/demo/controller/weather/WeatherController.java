package com.example.demo.controller.weather;

import com.example.demo.controller.weather.converter.WeatherRequestConverter;
import com.example.demo.controller.weather.dto.CreateWeatherRequest;
import com.example.demo.controller.weather.dto.DeleteWeatherRequest;
import com.example.demo.controller.weather.dto.GetWeatherByCityAndDateRangeRequest;
import com.example.demo.controller.weather.dto.GetWeatherByCityAndDateRequest;
import com.example.demo.controller.weather.dto.GetWeatherByCityRequest;
import com.example.demo.controller.weather.dto.GetWeatherByIdRequest;
import com.example.demo.controller.weather.dto.UpdateWeatherRequest;
import com.example.demo.entity.Weather;
import com.example.demo.service.weather.WeatherService;
import com.example.demo.service.weather.arg.*;
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
        // Convert request to arg
        CreateWeatherArg arg = converter.toCreateArg(request);
        
        // Pass arg to service layer
        Weather savedWeather = weatherService.createWeather(arg);
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
        // Convert request to arg
        QueryWeatherByCityAndDateArg arg = converter.toQueryByCityAndDateArg(request);
        
        // Pass arg to service layer
        List<Weather> weatherList = weatherService.getWeatherByCityAndDate(arg);
        List<WeatherResponse> responseList = weatherList.stream()
                .map(WeatherResponse::fromEntity)
                .collect(Collectors.toList());
        
        return BaseResponse.success(responseList);
    }

    // Read - Get by city and date range
    @PostMapping("/getByCityAndDateRange")
    @Operation(summary = "Get weather records by city and date range")
    public BaseResponse<List<WeatherResponse>> getWeatherByCityAndDateRange(@RequestBody GetWeatherByCityAndDateRangeRequest request) {
        // Convert request to arg
        QueryWeatherByCityAndDateRangeArg arg = converter.toQueryByCityAndDateRangeArg(request);
        
        // Pass arg to service layer
        List<Weather> weatherList = weatherService.getWeatherByCityAndDateRange(arg);
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
            // Convert request to arg
            UpdateWeatherArg arg = converter.toUpdateArg(request);
            
            // Pass arg to service layer
            Weather updatedWeather = weatherService.updateWeather(arg);
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
