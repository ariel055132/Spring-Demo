package com.example.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * Valkey (Redis-compatible) Configuration
 * 
 * Configures Valkey for:
 * - Rate limiting (distributed request throttling)
 * - Caching (future use)
 * 
 * Connection details are configured in application.yml under spring.data.redis
 * For local development, override with application-local.yml or environment variables
 */
@Configuration
public class ValkeyConfig {

    // Redis connection and templates are auto-configured by Spring Boot from application.yml
    // No manual bean configuration needed

    /**
     * Rate limiter key resolver
     * 
     * Strategy: Use IP address for rate limiting
     * Alternative strategies:
     * - JWT subject (authenticated users)
     * - API key (if using API key authentication)
     * - Combination of IP + user (for better granularity)
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest()
                .getRemoteAddress() != null 
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
            return Mono.just(ip);
        };
    }

    /**
     * Alternative: JWT-based key resolver (uncomment to use authenticated user)
     * Requires JWT authentication to be implemented first
     */
    /*
    @Bean
    public KeyResolver jwtKeyResolver() {
        return exchange -> {
            return exchange.getPrincipal()
                .map(Principal::getName)
                .defaultIfEmpty("anonymous");
        };
    }
    */
}
