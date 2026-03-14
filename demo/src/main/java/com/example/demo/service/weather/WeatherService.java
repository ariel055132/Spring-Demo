package com.example.demo.service.weather;

import com.example.demo.entity.Weather;
import com.example.demo.repository.WeatherRepository;
import com.example.demo.service.weather.arg.CreateWeatherArg;
import com.example.demo.service.weather.arg.DeleteWeatherArg;
import com.example.demo.service.weather.arg.ReadWeatherArg;
import com.example.demo.service.weather.arg.UpdateWeatherArg;
import com.example.demo.service.weather.response.WeatherResponse;
import com.example.demo.util.api.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    @Autowired
    private WeatherRepository weatherRepository;

    // Create
    public BaseResponse<WeatherResponse> create(CreateWeatherArg arg) {
        Weather weather = new Weather();
        weather.setCity(arg.getCity());
        weather.setTempLo(arg.getTempLo());
        weather.setTempHi(arg.getTempHi());
        weather.setPrcp(arg.getPrcp());
        weather.setDate(arg.getDate());
        
        Weather savedWeather = weatherRepository.save(weather);
        WeatherResponse response = WeatherResponse.fromEntity(savedWeather);
        return BaseResponse.success("Weather record created successfully", response);
    }

    // Read - Get by city and date
    public BaseResponse<List<WeatherResponse>> read(ReadWeatherArg arg) {
        List<Weather> weatherList = weatherRepository.findByCityAndDate(arg.getCity(), arg.getDate());
        
        if (weatherList.isEmpty()) {
            return BaseResponse.error("No weather records found for city: " + arg.getCity() + " on date: " + arg.getDate());
        }
        
        List<WeatherResponse> responseList = weatherList.stream()
                .map(WeatherResponse::fromEntity)
                .collect(Collectors.toList());
        return BaseResponse.success(responseList);
    }

    // Update
    public BaseResponse<WeatherResponse> update(UpdateWeatherArg arg) {
        try {
            List<Weather> weatherList = weatherRepository.findByCityAndDate(arg.getCity(), arg.getDate());
            
            if (weatherList.isEmpty()) {
                return BaseResponse.error("No weather record found for city: " + arg.getCity() + " on date: " + arg.getDate());
            }
            
            // Update the first matching record
            Weather weather = weatherList.get(0);
            weather.setTempLo(arg.getTempLo());
            weather.setTempHi(arg.getTempHi());
            weather.setPrcp(arg.getPrcp());

            Weather updatedWeather = weatherRepository.save(weather);
            WeatherResponse response = WeatherResponse.fromEntity(updatedWeather);
            return BaseResponse.success("Weather record updated successfully", response);
        } catch (RuntimeException e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    // Delete by city and date
    public BaseResponse<Void> delete(DeleteWeatherArg arg) {
        try {
            weatherRepository.deleteByCityAndDate(arg.getCity(), arg.getDate());
            return BaseResponse.success("Weather record(s) deleted successfully for city: " + arg.getCity() + " on date: " + arg.getDate(), null);
        } catch (Exception e) {
            return BaseResponse.error("Failed to delete weather record: " + e.getMessage());
        }
    }
}
