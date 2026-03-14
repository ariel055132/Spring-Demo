package com.example.demo.controller.weather;

import com.example.demo.controller.weather.converter.WeatherRequestConverter;
import com.example.demo.controller.weather.dto.CreateWeatherRequest;
import com.example.demo.controller.weather.dto.DeleteWeatherRequest;
import com.example.demo.controller.weather.dto.ReadWeatherRequest;
import com.example.demo.controller.weather.dto.UpdateWeatherRequest;
import com.example.demo.foundation.api.BaseResponse;
import com.example.demo.service.weather.WeatherService;
import com.example.demo.service.weather.arg.CreateWeatherArg;
import com.example.demo.service.weather.arg.DeleteWeatherArg;
import com.example.demo.service.weather.arg.ReadWeatherArg;
import com.example.demo.service.weather.arg.UpdateWeatherArg;
import com.example.demo.service.weather.response.WeatherResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public BaseResponse<WeatherResponse> create(@RequestBody CreateWeatherRequest request) {
        CreateWeatherArg arg = converter.toCreateArg(request);
        return weatherService.create(arg);
    }

    // Read
    @PostMapping("/read")
    @Operation(summary = "Get weather records by city and date")
    public BaseResponse<List<WeatherResponse>> read(@RequestBody ReadWeatherRequest request) {
        ReadWeatherArg arg = converter.toReadArg(request);
        return weatherService.read(arg);
    }

    // Update
    @PostMapping("/update")
    @Operation(summary = "Update an existing weather record")
    public BaseResponse<WeatherResponse> update(@RequestBody UpdateWeatherRequest request) {
        UpdateWeatherArg arg = converter.toUpdateArg(request);
        return weatherService.update(arg);
    }

    // Delete
    @PostMapping("/delete")
    @Operation(summary = "Delete weather records by city and date")
    public BaseResponse<Void> delete(@RequestBody DeleteWeatherRequest request) {
        DeleteWeatherArg arg = converter.toDeleteArg(request);
        return weatherService.delete(arg);
    }
}
