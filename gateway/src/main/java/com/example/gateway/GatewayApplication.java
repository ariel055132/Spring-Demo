package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway Application
 * 
 * Spring Cloud Gateway providing:
 * - Request routing to backend services
 * - JWT authentication and authorization
 * - Rate limiting with Valkey (Redis-compatible)
 * - Request/Response transformation
 * - Logging and monitoring
 * - API versioning
 * - CORS handling
 */
@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}
