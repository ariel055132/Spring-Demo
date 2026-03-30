package com.example.demo.service;

import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.foundation.util.LogUtil;

import java.util.concurrent.TimeUnit;

@Service
public class ValkeyService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Set a key-value pair
     */
    public void set(@NotBlank String key, @NotBlank String value) {
        LogUtil.debugInfo("Setting key: {} with value: {}", key, value);
        if (key != null && value != null) {
            redisTemplate.opsForValue().set(key, value);
            LogUtil.addInfo("Successfully set key: {}", key);
        } else {
            LogUtil.debugInfo("key or value is null");
        }
    }

    /**
     * Set a key-value pair with expiration time
     */
    public void setWithExpiry(String key, String value, long timeout, TimeUnit timeUnit) {
        LogUtil.debugInfo("Setting key: {} with value: {} and expiry: {} {}", key, value, timeout, timeUnit);
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        LogUtil.addInfo("Successfully set key: {} with expiry", key);
    }

    /**
     * Get value by key
     */
    public String get(String key) {
        LogUtil.debugInfo("Getting value for key: {}", key);
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            LogUtil.debugInfo("Found value for key: {}", key);
        } else {
            LogUtil.wrongInfo("Key not found: {}", key);
        }
        return value != null ? value.toString() : null;
    }

    /**
     * Delete a key
     */
    public Boolean delete(String key) {
        LogUtil.debugInfo("Deleting key: {}", key);
        Boolean result = redisTemplate.delete(key);
        LogUtil.addInfo("Key {} deletion result: {}", key, result);
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
        LogUtil.addInfo("Testing Valkey connection...");
        try {
            redisTemplate.opsForValue().set("test_connection", "success");
            String result = (String) redisTemplate.opsForValue().get("test_connection");
            redisTemplate.delete("test_connection");
            LogUtil.addInfo("Valkey connection test successful!");
            return "Connection successful! Test value: " + result;
        } catch (Exception e) {
            LogUtil.wrongInfo("Valkey connection test failed: {}", e.getMessage(), e);
            return "Connection failed: " + e.getMessage();
        }
    }
}
