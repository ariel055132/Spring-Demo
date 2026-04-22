package com.example.foundation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Valkey Cache Configuration for Foundation Library
 * Provides Redis-compatible caching using Valkey as the backend
 * 
 * This configuration is automatically enabled when:
 * - spring.cache.type=redis is set in application.properties
 * - Redis/Valkey connection is configured
 * 
 * @EnableCaching is included to enable caching for the entire application
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class ValkeyCacheConfig implements CachingConfigurer {

    private final RedisConnectionFactory connectionFactory;
    // Injecting the Spring-managed ObjectMapper ensures the same configuration (e.g. JavaTimeModule
    // for LocalDate/LocalDateTime) is used for both application serialization and cache serialization.
    // Without this, GenericJackson2JsonRedisSerializer would create its own unconfigured ObjectMapper,
    // causing a format mismatch when date fields are read back from the cache.
    private final ObjectMapper objectMapper;

    public ValkeyCacheConfig(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        this.connectionFactory = connectionFactory;
        this.objectMapper = objectMapper;
    }

    /**
     * Configure Valkey Cache Manager with default TTL
     * 
     * Default TTL: 30 minutes for all caches
     * 
     * @return Configured cache manager
     */
    @Bean
    @Override
    public CacheManager cacheManager() {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper))
                )
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .transactionAware()
                .build();
    }
}
