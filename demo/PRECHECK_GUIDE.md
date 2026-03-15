# WeatherChecker - Custom Message Validation Guide

## Overview
The `WeatherChecker` (located in `com.example.demo.service.weather.checker`) provides specific validation methods for Weather entity operations with **custom error messages**. Users must provide their own error messages for better context and clarity.

## Key Features
- ✅ **Custom Messages Required**: All error messages are typed by the user
- ✅ **Specific Methods**: Dedicated checker methods for Weather operations
- ✅ **Clear Exception Handling**: DuplicateDataException and DataNotFoundException

## WeatherChecker Methods

### 1. CreateWeatherChecker
Validates that weather data doesn't already exist before creating.

```java
@Autowired
private WeatherChecker weatherChecker;

public BaseResponse<WeatherResponse> create(CreateWeatherArg arg) {
    // User provides custom error message
    weatherChecker.CreateWeatherChecker(arg, 
        String.format("Weather data already exists for %s on %s", 
            arg.getCity(), arg.getDate()));
    
    // Business logic continues if no duplicate found
    Weather weather = new Weather();
    // ... save weather
}
```

**Behavior:**
- Checks if Weather with same `city` and `date` exists
- Throws `DuplicateDataException` with your custom message if duplicate found
- Continues execution if no duplicate

### 2. UpdateWeatherChecker
Validates that weather data exists before updating.

```java
public BaseResponse<WeatherResponse> update(UpdateWeatherArg arg) {
    // User provides custom error message
    weatherChecker.UpdateWeatherChecker(arg,
        String.format("No weather data found for %s on %s. Unable to update.", 
            arg.getCity(), arg.getDate()));
    
    // Business logic continues if data found
    // ... update weather
}
```

**Behavior:**
- Checks if Weather with specified `city` and `date` exists
- Throws `DataNotFoundException` with your custom message if not found
- Continues execution if found

### 3. DeleteWeatherChecker
Validates that weather data exists before deleting.

```java
public BaseResponse<Void> delete(DeleteWeatherArg arg) {
    // User provides custom error message
    weatherChecker.DeleteWeatherChecker(arg,
        String.format("No weather data found for %s on %s. Unable to delete.", 
            arg.getCity(), arg.getDate()));
    
    // Business logic continues if data found
    // ... delete weather
}
```

**Behavior:**
- Checks if Weather with specified `city` and `date` exists
- Throws `DataNotFoundException` with your custom message if not found
- Continues execution if found

## Exception Classes

### DuplicateDataException
```java
public class DuplicateDataException extends RuntimeException {
    public DuplicateDataException(String message) {
        super(message);
    }
}
```

**When thrown:** During CREATE operations when duplicate data is found
**HTTP Status:** 409 Conflict (handled by GlobalExceptionHandler)

### DataNotFoundException
```java
public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String message) {
        super(message);
    }
}
```

**When thrown:** During UPDATE/DELETE operations when data doesn't exist
**HTTP Status:** 404 Not Found (handled by GlobalExceptionHandler)

## Response Examples

### Success Response (CREATE)
```json
{
  "success": true,
  "message": "Weather record created successfully",
  "data": {
    "id": 1,
    "city": "Hong Kong",
    "tempLo": 11,
    "tempHi": 12,
    "prcp": 0.5,
    "date": "2026-03-15"
  }
}
```
HTTP Status: 200 OK

### Duplicate Error Response (CREATE)
```json
{
  "success": false,
  "message": "Weather data already exists for Hong Kong on 2026-03-15",
  "data": null
}
```
HTTP Status: 409 Conflict

### Not Found Error Response (UPDATE/DELETE)
```json
{
  "success": false,
  "message": "No weather data found for Hong Kong on 2026-03-15. Unable to update.",
  "data": null
}
```
HTTP Status: 404 Not Found

## Creating Your Own Checker

You can create similar checker classes for other entities in their respective service packages:

```java
package com.example.demo.service.yourentity.checker;

import com.example.demo.foundation.checker.DataNotFoundException;
import com.example.demo.foundation.checker.DuplicateDataException;

@Component
public class YourEntityChecker {
    
    @Autowired
    private YourEntityRepository repository;
    
    public void CreateYourEntityChecker(YourCreateArg arg, String customMessage) {
        List<YourEntity> existing = repository.findByUniqueFields(...);
        
        if (!existing.isEmpty()) {
            throw new DuplicateDataException(customMessage);
        }
    }
    
    public void UpdateYourEntityChecker(YourUpdateArg arg, String customMessage) {
        List<YourEntity> existing = repository.findByIdentifyingFields(...);
        
        if (existing.isEmpty()) {
            throw new DataNotFoundException(customMessage);
        }
    }
    
    public void DeleteYourEntityChecker(YourDeleteArg arg, String customMessage) {
        List<YourEntity> existing = repository.findByIdentifyingFields(...);
        
        if (existing.isEmpty()) {
            throw new DataNotFoundException(customMessage);
        }
    }
}
```

## Best Practices

### 1. Provide Clear Custom Messages
```java
// Good: Specific and actionable
weatherChecker.CreateWeatherChecker(arg, 
    String.format("Weather data already exists for %s on %s", 
        arg.getCity(), arg.getDate()));

// Avoid: Generic messages
weatherChecker.CreateWeatherChecker(arg, "Duplicate found");
```

### 2. Include Relevant Context
```java
// Good: Helps user understand what's missing
weatherChecker.UpdateWeatherChecker(arg,
    String.format("Cannot update: No weather record found for %s on %s", 
        arg.getCity(), arg.getDate()));

// Good: Includes action hint
weatherChecker.DeleteWeatherChecker(arg,
    String.format("Unable to delete: Weather data for %s on %s does not exist", 
        arg.getCity(), arg.getDate()));
```

### 3. Use String.format() for Dynamic Messages
```java
// Recommended: Dynamic values in message
String.format("Weather data already exists for %s on %s", 
    arg.getCity(), arg.getDate())

// Alternative: String concatenation
"Weather data already exists for " + arg.getCity() + " on " + arg.getDate()
```

## GlobalExceptionHandler

The `GlobalExceptionHandler` automatically converts exceptions to proper HTTP responses:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<BaseResponse<Void>> handleDuplicateDataException(
            DuplicateDataException ex) {
        BaseResponse<Void> response = BaseResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleDataNotFoundException(
            DataNotFoundException ex) {
        BaseResponse<Void> response = BaseResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
```

## Testing

### Test Duplicate Creation
1. Create a weather record for "Hong Kong" on "2026-03-15"
2. Try to create the same record again
3. Expected: 409 Conflict with message "Weather data already exists for Hong Kong on 2026-03-15"

### Test Update Non-existent
1. Try to update weather for "Tokyo" on "2026-01-01" (doesn't exist)
2. Expected: 404 Not Found with message "No weather data found for Tokyo on 2026-01-01. Unable to update."

### Test Delete Non-existent
1. Try to delete weather for "Paris" on "2026-02-20" (doesn't exist)
2. Expected: 404 Not Found with message "No weather data found for Paris on 2026-02-20. Unable to delete."

## Architecture Benefits

1. **User-Controlled Messages**: You define exactly what error message users see
2. **Entity-Specific Validation**: WeatherChecker is tailored for Weather entity
3. **Clean Separation**: Validation logic separated from business logic
4. **Reusable Pattern**: Easy to create checkers for other entities
5. **Consistent Error Handling**: GlobalExceptionHandler ensures uniform responses

## Summary

The WeatherChecker approach provides:
- **Custom error messages** typed by developers
- **Specific validation methods** for each operation type
- **Clear exception handling** with appropriate HTTP status codes
- **Simple and direct** - just call the checker method with your message

This gives you full control over user-facing error messages while maintaining clean, validated code.

