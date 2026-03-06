package com.example.demo.util;

/**
 * Simple example demonstrating LogUtil usage
 * Logger is automatically created based on the calling class
 */
public class LogUtilExample {
    
    public void demonstrateLogging() {
        
        // 1. INFO level logging - no logger parameter needed!
        LogUtil.addInfo("Application started successfully");
        LogUtil.addInfo("User {} logged in from IP: {}", "john123", "192.168.1.100");
        
        // 2. ERROR/WARNING level logging
        LogUtil.wrongInfo("Failed to connect to database");
        LogUtil.wrongInfo("Invalid input for user: {}", "john123");
        
        // 3. ERROR with exception
        try {
            throw new RuntimeException("Something went wrong");
        } catch (Exception e) {
            LogUtil.wrongInfo("Error processing request", e);
        }
        
        // 4. DEBUG level logging
        LogUtil.debugInfo("Entering method: processOrder");
        LogUtil.debugInfo("Processing order {} for user {}", "ORD123", "john123");
    }
    
    // Real-world example in a service method
    public String processOrder(String orderId, double amount) {
        LogUtil.addInfo("Processing order: {} with amount: {}", orderId, amount);
        
        try {
            // Your business logic here
            LogUtil.debugInfo("Validating order: {}", orderId);
            
            // ... processing ...
            
            LogUtil.addInfo("Order {} processed successfully", orderId);
            return "Order processed";
            
        } catch (Exception e) {
            LogUtil.wrongInfo("Failed to process order: {}", orderId, e);
            throw e;
        }
    }
    
    // Example in a cache retrieval method
    public String getUserFromCache(String userId) {
        LogUtil.debugInfo("Looking up user in cache: {}", userId);
        
        String userData = checkCache(userId);
        
        if (userData != null) {
            LogUtil.addInfo("Cache hit for user: {}", userId);
            return userData;
        } else {
            LogUtil.wrongInfo("Cache miss for user: {}", userId);
            return fetchFromDatabase(userId);
        }
    }
    
    private String checkCache(String key) {
        return null; // Simulate cache miss
    }
    
    private String fetchFromDatabase(String userId) {
        LogUtil.debugInfo("Fetching user from database: {}", userId);
        return "UserData";
    }
    
    public static void main(String[] args) {
        LogUtilExample example = new LogUtilExample();
        example.demonstrateLogging();
        example.processOrder("ORD123", 99.99);
        example.getUserFromCache("USER456");
    }
}
