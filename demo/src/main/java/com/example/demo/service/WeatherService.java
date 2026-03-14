package com.example.demo.service;

import com.example.demo.entity.Weather;
import com.example.demo.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class WeatherService {

    @Autowired
    private WeatherRepository weatherRepository;

    // Create or Update
    public Weather saveWeather(Weather weather) {
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
    public List<Weather> getWeatherByCityAndDate(String city, LocalDate date) {
        return weatherRepository.findByCityAndDateGreaterThanEqual(city, date);
    }

    // Read - Get by city and date range
    public List<Weather> getWeatherByCityAndDateRange(String city, LocalDate startDate, LocalDate endDate) {
        return weatherRepository.findByCityAndDateBetween(city, startDate, endDate);
    }

    // Update
    public Weather updateWeather(Long id, Weather weatherDetails) {
        Weather weather = weatherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Weather not found with id: " + id));

        weather.setCity(weatherDetails.getCity());
        weather.setTempLo(weatherDetails.getTempLo());
        weather.setTempHi(weatherDetails.getTempHi());
        weather.setPrcp(weatherDetails.getPrcp());
        weather.setDate(weatherDetails.getDate());

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
