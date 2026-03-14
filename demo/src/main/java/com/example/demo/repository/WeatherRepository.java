package com.example.demo.repository;

import com.example.demo.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {
    
    // Find weather data by city and date greater than or equal to specified date
    List<Weather> findByCityAndDateGreaterThanEqual(String city, LocalDate date);
    
    // Alternative using @Query annotation (more explicit)
    @Query("SELECT w FROM Weather w WHERE w.city = :city AND w.date >= :date")
    List<Weather> findWeatherByCityAndDateAfter(@Param("city") String city, @Param("date") LocalDate date);
    
    // Bonus: Find by city only
    List<Weather> findByCity(String city);
    
    // Find by city and exact date
    List<Weather> findByCityAndDate(String city, LocalDate date);
    
    // Delete by city and exact date
    @Transactional
    void deleteByCityAndDate(String city, LocalDate date);
    
    // Bonus: Find by date range
    List<Weather> findByCityAndDateBetween(String city, LocalDate startDate, LocalDate endDate);
}
