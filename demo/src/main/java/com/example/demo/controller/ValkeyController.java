package com.example.demo.controller;

import com.example.demo.service.ValkeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/valkey")
public class ValkeyController {

    @Autowired
    private ValkeyService valkeyService;

    /**
     * Test Valkey connection
     * GET /api/valkey/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testConnection() {
        Map<String, String> response = new HashMap<>();
        String result = valkeyService.testConnection();
        response.put("status", result);
        return ResponseEntity.ok(response);
    }

    /**
     * Set a key-value pair
     * POST /api/valkey/set?key=mykey&value=myvalue
     */
    @PostMapping("/set")
    public ResponseEntity<Map<String, String>> setValue(
            @RequestParam String key,
            @RequestParam String value,
            @RequestParam(required = false) Long expirySeconds) {
        
        Map<String, String> response = new HashMap<>();
        
        if (expirySeconds != null && expirySeconds > 0) {
            valkeyService.setWithExpiry(key, value, expirySeconds, TimeUnit.SECONDS);
            response.put("message", "Key set with expiry of " + expirySeconds + " seconds");
        } else {
            valkeyService.set(key, value);
            response.put("message", "Key set successfully");
        }
        
        response.put("key", key);
        response.put("value", value);
        return ResponseEntity.ok(response);
    }

    /**
     * Get value by key
     * GET /api/valkey/get?key=mykey
     */
    @GetMapping("/get")
    public ResponseEntity<Map<String, String>> getValue(@RequestParam String key) {
        Map<String, String> response = new HashMap<>();
        String value = valkeyService.get(key);
        
        if (value != null) {
            response.put("key", key);
            response.put("value", value);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Key not found");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a key
     * DELETE /api/valkey/delete?key=mykey
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteKey(@RequestParam String key) {
        Map<String, String> response = new HashMap<>();
        Boolean deleted = valkeyService.delete(key);
        
        response.put("key", key);
        response.put("deleted", deleted.toString());
        return ResponseEntity.ok(response);
    }

    /**
     * Check if key exists
     * GET /api/valkey/exists?key=mykey
     */
    @GetMapping("/exists")
    public ResponseEntity<Map<String, Object>> keyExists(@RequestParam String key) {
        Map<String, Object> response = new HashMap<>();
        Boolean exists = valkeyService.hasKey(key);
        
        response.put("key", key);
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    /**
     * Increment a numeric value
     * POST /api/valkey/increment?key=counter
     */
    @PostMapping("/increment")
    public ResponseEntity<Map<String, Object>> increment(@RequestParam String key) {
        Map<String, Object> response = new HashMap<>();
        Long value = valkeyService.increment(key);
        
        response.put("key", key);
        response.put("value", value);
        return ResponseEntity.ok(response);
    }

    /**
     * Decrement a numeric value
     * POST /api/valkey/decrement?key=counter
     */
    @PostMapping("/decrement")
    public ResponseEntity<Map<String, Object>> decrement(@RequestParam String key) {
        Map<String, Object> response = new HashMap<>();
        Long value = valkeyService.decrement(key);
        
        response.put("key", key);
        response.put("value", value);
        return ResponseEntity.ok(response);
    }

    /**
     * Get TTL for a key
     * GET /api/valkey/ttl?key=mykey
     */
    @GetMapping("/ttl")
    public ResponseEntity<Map<String, Object>> getTTL(@RequestParam String key) {
        Map<String, Object> response = new HashMap<>();
        Long ttl = valkeyService.getExpire(key);
        
        response.put("key", key);
        response.put("ttl_seconds", ttl);
        return ResponseEntity.ok(response);
    }
}
