package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ValkeyService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Set a key-value pair
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * Set a key-value pair with expiration time
     */
    public void setWithExpiry(String key, String value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * Get value by key
     */
    public String get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Delete a key
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
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
        try {
            redisTemplate.opsForValue().set("test_connection", "success");
            String result = (String) redisTemplate.opsForValue().get("test_connection");
            redisTemplate.delete("test_connection");
            return "Connection successful! Test value: " + result;
        } catch (Exception e) {
            return "Connection failed: " + e.getMessage();
        }
    }
}
