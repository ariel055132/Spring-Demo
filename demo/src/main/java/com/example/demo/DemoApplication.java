package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.demo", "com.example.foundation"})
public class DemoApplication {

	public static void main(String[] args) {
		System.out.println("Application is activated");
		SpringApplication.run(DemoApplication.class, args);
	}

}
