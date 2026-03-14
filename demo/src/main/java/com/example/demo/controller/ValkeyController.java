package com.example.demo.controller;

import com.example.demo.foundation.util.LogUtil;
import com.example.demo.service.ValkeyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/valkey")
@Tag(name = "Valkey Operations", description = "APIs for interacting with Valkey cache")
public class ValkeyController {

    @Autowired
    private ValkeyService valkeyService;

    @Operation(
            summary = "Test Valkey Connection",
            description = "Verifies connectivity to the Valkey server by performing a test read/write operation"
    )
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testConnection() {
        Map<String, String> response = new HashMap<>();
        String result = valkeyService.testConnection();
        response.put("status", result);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Set Key-Value Pair",
            description = "Store a key-value pair in Valkey with optional expiration time"
    )
    @PostMapping("/set")
    public ResponseEntity<Map<String, String>> setValue(
            @Parameter(description = "The key to store") @RequestParam String key,
            @Parameter(description = "The value to store") @RequestParam String value,
            @Parameter(description = "Optional expiration time in seconds") @RequestParam(required = false) Long expirySeconds) {
        
        LogUtil.debugInfo("Setting key: {} with value: {}", key, value);
        
        try {
            Map<String, String> response = new HashMap<>();
            if (expirySeconds != null && expirySeconds > 0) {
                valkeyService.setWithExpiry(key, value, expirySeconds, TimeUnit.SECONDS);
                response.put("message", "Key set with expiry of " + expirySeconds + " seconds");
                LogUtil.addInfo("Key {} set with expiry of {} seconds", key, expirySeconds);
            } else {
                valkeyService.set(key, value);
                response.put("message", "Key set successfully");
                LogUtil.addInfo("Key {} set successfully", key);
            }
            
            response.put("key", key);
            response.put("value", value);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to set key: {}", key, e);
            throw e;
        }
    }

    @Operation(
            summary = "Get Value by Key",
            description = "Retrieve the value associated with a key from Valkey"
    )
    @GetMapping("/get")
    public ResponseEntity<Map<String, String>> getValue(
            @Parameter(description = "The key to retrieve") @RequestParam String key) {
        
        LogUtil.debugInfo("Getting value for key: {}", key);
        
        Map<String, String> response = new HashMap<>();
        String value = valkeyService.get(key);
        
        if (value != null) {
            LogUtil.addInfo("Retrieved value for key: {}", key);
            
            response.put("key", key);
            response.put("value", value);
            
            return ResponseEntity.ok(response);
        } else {
            LogUtil.wrongInfo("Key not found: {}", key);
            
            response.put("error", "Key not found");
            
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Delete Key",
            description = "Remove a key and its associated value from Valkey"
    )
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteKey(
            @Parameter(description = "The key to delete") @RequestParam String key) {
        Map<String, String> response = new HashMap<>();
        Boolean deleted = valkeyService.delete(key);
        
        response.put("key", key);
        response.put("deleted", deleted.toString());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Check Key Existence",
            description = "Verify whether a key exists in Valkey"
    )
    @GetMapping("/exists")
    public ResponseEntity<Map<String, Object>> keyExists(
            @Parameter(description = "The key to check") @RequestParam String key) {
        Map<String, Object> response = new HashMap<>();
        Boolean exists = valkeyService.hasKey(key);
        
        response.put("key", key);
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Increment Counter",
            description = "Increment the numeric value of a key by 1. Creates the key with value 1 if it doesn't exist"
    )
    @PostMapping("/increment")
    public ResponseEntity<Map<String, Object>> increment(
            @Parameter(description = "The key to increment") @RequestParam String key) {
        Map<String, Object> response = new HashMap<>();
        Long value = valkeyService.increment(key);
        
        response.put("key", key);
        response.put("value", value);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Decrement Counter",
            description = "Decrement the numeric value of a key by 1. Creates the key with value -1 if it doesn't exist"
    )
    @PostMapping("/decrement")
    public ResponseEntity<Map<String, Object>> decrement(
            @Parameter(description = "The key to decrement") @RequestParam String key) {
        Map<String, Object> response = new HashMap<>();
        Long value = valkeyService.decrement(key);
        
        response.put("key", key);
        response.put("value", value);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get Time-To-Live",
            description = "Get the remaining time-to-live (TTL) in seconds for a key with expiration"
    )
    @GetMapping("/ttl")
    public ResponseEntity<Map<String, Object>> getTTL(
            @Parameter(description = "The key to check TTL for") @RequestParam String key) {
        Map<String, Object> response = new HashMap<>();
        Long ttl = valkeyService.getExpire(key);
        
        response.put("key", key);
        response.put("ttl_seconds", ttl);
        return ResponseEntity.ok(response);
    }
}
