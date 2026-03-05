package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ValkeyService {

    private static final Logger logger = LoggerFactory.getLogger(ValkeyService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Set a key-value pair
     */
    public void set(String key, String value) {
        logger.debug("Setting key: {} with value: {}", key, value);
        redisTemplate.opsForValue().set(key, value);
        logger.info("Successfully set key: {}", key);
    }

    /**
     * Set a key-value pair with expiration time
     */
    public void setWithExpiry(String key, String value, long timeout, TimeUnit timeUnit) {
        logger.debug("Setting key: {} with value: {} and expiry: {} {}", key, value, timeout, timeUnit);
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        logger.info("Successfully set key: {} with expiry", key);
    }

    /**
     * Get value by key
     */
    public String get(String key) {
        logger.debug("Getting value for key: {}", key);
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            logger.debug("Found value for key: {}", key);
        } else {
            logger.warn("Key not found: {}", key);
        }
        return value != null ? value.toString() : null;
    }

    /**
     * Delete a key
     */
    public Boolean delete(String key) {
        logger.debug("Deleting key: {}", key);
        Boolean result = redisTemplate.delete(key);
        logger.info("Key {} deletion result: {}", key, result);
        return result;
    }

    /**
     * Check if key exists
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * Set expiration time for a key
     */
    public Boolean expire(String key, long timeout, TimeUnit timeUnit) {
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    /**
     * Get time to live for a key
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * Increment a numeric value
     */
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * Decrement a numeric value
     */
    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    /**
     * Test connection to Valkey
     */
    public String testConnection() {
        logger.info("Testing Valkey connection...");
        try {
            redisTemplate.opsForValue().set("test_connection", "success");
            String result = (String) redisTemplate.opsForValue().get("test_connection");
            redisTemplate.delete("test_connection");
            logger.info("Valkey connection test successful!");
            return "Connection successful! Test value: " + result;
        } catch (Exception e) {
            logger.error("Valkey connection test failed: {}", e.getMessage(), e);
            return "Connection failed: " + e.getMessage();
        }
    }
}
