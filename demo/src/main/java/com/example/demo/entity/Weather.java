package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "weather")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Weather {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 80)
    private String city;
    
    @Column(name = "temp_lo")
    private Integer tempLo;  // low temperature
    
    @Column(name = "temp_hi")
    private Integer tempHi;  // high temperature
    
    @Column(columnDefinition = "real")
    private Float prcp;  // precipitation
    
    private LocalDate date;
}
