package com.example.demo.service.weather;

import com.example.demo.entity.Weather;
import com.example.demo.repository.WeatherRepository;
import com.example.demo.service.weather.arg.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WeatherService {

    @Autowired
    private WeatherRepository weatherRepository;

    // Create
    public Weather createWeather(CreateWeatherArg arg) {
        Weather weather = new Weather();
        weather.setCity(arg.getCity());
        weather.setTempLo(arg.getTempLo());
        weather.setTempHi(arg.getTempHi());
        weather.setPrcp(arg.getPrcp());
        weather.setDate(arg.getDate());
        
        return weatherRepository.save(weather);
    }

    // Read - Get all
    public List<Weather> getAllWeather() {
        return weatherRepository.findAll();
    }

    // Read - Get by ID
    public Optional<Weather> getWeatherById(Long id) {
        return weatherRepository.findById(id);
    }

    // Read - Get by city
    public List<Weather> getWeatherByCity(String city) {
        return weatherRepository.findByCity(city);
    }

    // Read - Get by city and date greater than or equal
    public List<Weather> getWeatherByCityAndDate(QueryWeatherByCityAndDateArg arg) {
        return weatherRepository.findByCityAndDateGreaterThanEqual(arg.getCity(), arg.getDate());
    }

    // Read - Get by city and date range
    public List<Weather> getWeatherByCityAndDateRange(QueryWeatherByCityAndDateRangeArg arg) {
        return weatherRepository.findByCityAndDateBetween(
                arg.getCity(), 
                arg.getStartDate(), 
                arg.getEndDate()
        );
    }

    // Update
    public Weather updateWeather(UpdateWeatherArg arg) {
        Weather weather = weatherRepository.findById(arg.getId())
                .orElseThrow(() -> new RuntimeException("Weather not found with id: " + arg.getId()));

        weather.setCity(arg.getCity());
        weather.setTempLo(arg.getTempLo());
        weather.setTempHi(arg.getTempHi());
        weather.setPrcp(arg.getPrcp());
        weather.setDate(arg.getDate());

        return weatherRepository.save(weather);
    }

    // Delete
    public void deleteWeather(Long id) {
        weatherRepository.deleteById(id);
    }

    // Delete all
    public void deleteAllWeather() {
        weatherRepository.deleteAll();
    }
}
