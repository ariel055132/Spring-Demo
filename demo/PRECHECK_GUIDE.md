# PreCheck Validation Framework - Complete Architecture Guide

## Overview
The `PreCheck` framework provides a **class-based, extensible validation system** using AOP. It validates data existence/duplication before CRUD operations using self-contained checker classes.

## Key Components

### 1. Foundation Layer (Generic)
- `@PreCheck` - Annotation accepting checker class reference
- `PreCheckHandler` - Interface with `doCheck(Object arg)` method
- `PreCheckAspect` - AOP interceptor (no entity-specific imports)
- `BaseChecker` - Abstract base implementing `PreCheckHandler`
- `GlobalExceptionHandler` - Converts exceptions to HTTP responses

### 2. Service Layer (Entity-Specific)
- `WeatherChecker` - Abstract base extending `BaseChecker` with repository access
- `CreateWeatherChecker` - Validates no duplicate data exists
- `UpdateWeatherChecker` - Validates data exists before update
- `DeleteWeatherChecker` - Validates data exists before delete
- `WeatherCheckMessageEnum` - Centralized error messages

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    @PreCheck Annotation                     │
│         @PreCheck(CreateWeatherChecker.class)               │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    PreCheckAspect (AOP)                     │
│  - Intercepts @PreCheck methods                             │
│  - Gets checker class from annotation                       │
│  - Retrieves checker bean from Spring context               │
│  - Calls doCheck(arg) on checker instance                   │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│              PreCheckHandler Interface                      │
│                 void doCheck(Object arg)                    │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│           BaseChecker (abstract, implements                 │
│                  PreCheckHandler)                           │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│         WeatherChecker (abstract, extends BaseChecker)      │
│         - weatherRepository access                          │
│         - isWeatherExist() helper method                    │
└────────────────────┬────────────────────────────────────────┘
                     │
         ┌───────────┼───────────┐
         ▼           ▼           ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│   Create     │ │   Update     │ │   Delete     │
│WeatherChecker│ │WeatherChecker│ │WeatherChecker│
└──────────────┘ └──────────────┘ └──────────────┘
```

## Core Interfaces and Classes

### PreCheckHandler Interface (Foundation Layer)

```java
package com.example.demo.foundation.checker;

/**
 * Interface for handling pre-check validation logic
 * Implement this interface to create custom validation handlers
 */
public interface PreCheckHandler {
    
    /**
     * Perform validation check
     * Throws appropriate exception if validation fails
     * 
     * @param arg The argument object containing data to validate
     */
    void doCheck(Object arg);
}
```

### @PreCheck Annotation

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreCheck {
    /**
     * The checker class that implements PreCheckHandler
     * @return The checker class to use for validation
     */
    Class<? extends PreCheckHandler> value();
}
```

### BaseChecker (Abstract Base Class)

```java
@Component
public abstract class BaseChecker<T> implements PreCheckHandler<Object> {
    
    public void doCheck(Object arg) {
        @SuppressWarnings("unchecked")
        T typedArg = (T) arg;
        doCheckInternal(typedArg);
    }
    
    protected abstract void doCheckInternal(T arg);
}
```

BaseChecker provides a template method pattern where `doCheck` casts the argument and delegates to `doCheckInternal` for subclasses to implement their validation logic.

## Exception Handling

### Standard Java Exceptions

Instead of custom exceptions, the framework uses standard Java exceptions:

| Exception Type            | Use Case           | HTTP Status  |
|--------------------------|-------------------|--------------|
| `IllegalStateException`  | Duplicate data    | 409 Conflict |
| `IllegalArgumentException` | Data not found  | 404 Not Found |

### GlobalExceptionHandler

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handle illegal state exceptions (duplicate data, CREATE operations)
     * Returns 409 Conflict
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseBody
    public ResponseEntity<BaseResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        BaseResponse<Void> response = BaseResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    /**
     * Handle illegal argument exceptions (data not found, UPDATE/DELETE operations)
     * Returns 404 Not Found
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<BaseResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        BaseResponse<Void> response = BaseResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
```
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

## Weather Entity Implementation Example

### WeatherChecker (Abstract Base)

```java
@Component
public abstract class WeatherChecker extends BaseChecker {
    
    @Autowired
    protected WeatherRepository weatherRepository;

    /**
     * Check if weather data exists for given city and date
     */
    protected boolean isWeatherExist(String city, LocalDate date) {
        Weather weather = weatherRepository.findByCityAndDate(city, date);
        return weather != null;
    }
}
```

### CreateWeatherChecker

```java
@Component
public class CreateWeatherChecker extends WeatherChecker {
    
    @Override
    public void doCheck(Object arg) {
        if (!(arg instanceof CreateWeatherArg)) {
            throw new IllegalArgumentException("CreateWeatherChecker requires CreateWeatherArg");
        }
        
        CreateWeatherArg createArg = (CreateWeatherArg) arg;
        
        if (isWeatherExist(createArg.getCity(), createArg.getDate())) {
            String errorMessage = WeatherCheckMessageEnum.CREATE_DUPLICATE.getMessage(
                createArg.getCity(), 
                createArg.getDate().toString()
            );
            throw new IllegalStateException(errorMessage);
        }
    }
}
```

### UpdateWeatherChecker

```java
@Component
public class UpdateWeatherChecker extends WeatherChecker {
    
    @Override
    public void doCheck(Object arg) {
        if (!(arg instanceof UpdateWeatherArg)) {
            throw new IllegalArgumentException("UpdateWeatherChecker requires UpdateWeatherArg");
        }
        
        UpdateWeatherArg updateArg = (UpdateWeatherArg) arg;
        
        if (!isWeatherExist(updateArg.getCity(), updateArg.getDate())) {
            String errorMessage = WeatherCheckMessageEnum.UPDATE_NOT_FOUND.getMessage(
                updateArg.getCity(), 
                updateArg.getDate().toString()
            );
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
```

### DeleteWeatherChecker

```java
@Component
public class DeleteWeatherChecker extends WeatherChecker {
    
    @Override
    public void doCheck(Object arg) {
        if (!(arg instanceof DeleteWeatherArg)) {
            throw new IllegalArgumentException("DeleteWeatherChecker requires DeleteWeatherArg");
        }
        
        DeleteWeatherArg deleteArg = (DeleteWeatherArg) arg;
        
        if (!isWeatherExist(deleteArg.getCity(), deleteArg.getDate())) {
            String errorMessage = WeatherCheckMessageEnum.DELETE_NOT_FOUND.getMessage(
                deleteArg.getCity(), 
                deleteArg.getDate().toString()
            );
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
```

### WeatherCheckMessageEnum

```java
public enum WeatherCheckMessageEnum implements CheckMessage {
    
    CREATE_DUPLICATE("Weather data already exists for {city} on {date}"),
    UPDATE_NOT_FOUND("No weather data found for {city} on {date}. Unable to update."),
    DELETE_NOT_FOUND("No weather data found for {city} on {date}. Unable to delete.");
    
    private final String messageTemplate;
    
    WeatherCheckMessageEnum(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }
    
    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }
    
    /**
     * Get message with placeholders replaced
     */
    public String getMessage(String city, String date) {
        return messageTemplate
                .replace("{city}", city != null ? city : "null")
                .replace("{date}", date != null ? date : "null");
    }
}
```

## Creating Your Own Checker for Other Entities

### Step 1: Create Entity-Specific Base Checker

```java
package com.example.demo.service.user.checker;

@Component
public abstract class UserChecker extends BaseChecker {
    
    @Autowired
    protected UserRepository userRepository;

    protected boolean isUserExist(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }
}
```

### Step 2: Create Message Enum

```java
public enum UserCheckMessageEnum {
    
    CREATE_DUPLICATE("User with email {email} already exists"),
    UPDATE_NOT_FOUND("User with ID {id} not found. Unable to update."),
    DELETE_NOT_FOUND("User with ID {id} not found. Unable to delete.");
    
    private final String messageTemplate;
    
    UserCheckMessageEnum(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }
    
    public String getMessage(String... params) {
        String result = messageTemplate;
        // Replace placeholders with actual values
        return result;
    }
}
```

### Step 3: Create Specific Checker Classes

```java
@Component
public class CreateUserChecker extends UserChecker {
    
    @Override
    public void doCheck(Object arg) {
        if (!(arg instanceof CreateUserArg)) {
            throw new IllegalArgumentException("CreateUserChecker requires CreateUserArg");
        }
        
        CreateUserArg createArg = (CreateUserArg) arg;
        
        if (isUserExist(createArg.getEmail())) {
            String errorMessage = UserCheckMessageEnum.CREATE_DUPLICATE
                .getMessage(createArg.getEmail());
            throw new IllegalStateException(errorMessage);
        }
    }
}
```

### Step 4: Use in Service

```java
@Service
public class UserService {
    
    @PreCheck(CreateUserChecker.class)
    public BaseResponse<UserResponse> create(CreateUserArg arg) {
        // Validation happens automatically - just write business logic
        User user = new User();
        user.setEmail(arg.getEmail());
        return userRepository.save(user);
    }
    
    @PreCheck(UpdateUserChecker.class)
    public BaseResponse<UserResponse> update(UpdateUserArg arg) {
        // Validation happens automatically
        User user = userRepository.findById(arg.getId()).get();
        user.setName(arg.getName());
        return userRepository.save(user);
    }
}
```

## Best Practices

### 1. Create Self-Contained Checker Classes
```java
// Good: Each checker is independent and self-contained
@Component
public class CreateWeatherChecker extends WeatherChecker {
    @Override
    public void doCheck(Object arg) {
        // Contains its own validation logic and error message
    }
}

// Avoid: Putting all logic in one checker class
```

### 2. Use Message Enums for Error Messages
```java
// Good: Centralized message enum with placeholders
public enum WeatherCheckMessageEnum {
    CREATE_DUPLICATE("Weather data already exists for {city} on {date}");
    
    public String getMessage(String city, String date) {
        return messageTemplate.replace("{city}", city).replace("{date}", date);
    }
}

// Avoid: Hardcoding messages in checker classes
throw new IllegalStateException("Duplicate found");  // Too generic
```

### 3. Keep Inheritance Hierarchy Clean
```
BaseChecker (implements PreCheckHandler)
    └── WeatherChecker (adds weatherRepository)
            ├── CreateWeatherChecker
            ├── UpdateWeatherChecker
            └── DeleteWeatherChecker
```

### 4. Use Standard Java Exceptions
```java
// For duplicates (CREATE operations)
throw new IllegalStateException(errorMessage);  // → 409 Conflict

// For not found (UPDATE/DELETE operations)
throw new IllegalArgumentException(errorMessage);  // → 404 Not Found
```

### 5. Type Check Arguments Early
```java
@Override
public void doCheck(Object arg) {
    if (!(arg instanceof CreateWeatherArg)) {
        throw new IllegalArgumentException("CreateWeatherChecker requires CreateWeatherArg");
    }
    // Safe to cast and proceed
}
```

## Testing

### Test Duplicate Creation
```bash
# 1. Create a weather record
POST /api/weather/create
{
  "city": "Hong Kong",
  "date": "2026-03-15",
  "tempLo": 11,
  "tempHi": 12,
  "prcp": 0.5
}
# Expected: 200 OK

# 2. Try to create the same record again
POST /api/weather/create
{
  "city": "Hong Kong",
  "date": "2026-03-15",
  "tempLo": 11,
  "tempHi": 12,
  "prcp": 0.5
}
# Expected: 409 Conflict
# Message: "Weather data already exists for Hong Kong on 2026-03-15"
```

### Test Update Non-existent
```bash
# Try to update weather that doesn't exist
POST /api/weather/update
{
  "city": "Tokyo",
  "date": "2026-01-01",
  "tempLo": 5,
  "tempHi": 10,
  "prcp": 0.0
}
# Expected: 404 Not Found
# Message: "No weather data found for Tokyo on 2026-01-01. Unable to update."
```

### Test Delete Non-existent
```bash
# Try to delete weather that doesn't exist
POST /api/weather/delete
{
  "city": "Paris",
  "date": "2026-02-20"
}
# Expected: 404 Not Found
# Message: "No weather data found for Paris on 2026-02-20. Unable to delete."
```

## Architecture Benefits

### 1. **Class-Based Design**
- `@PreCheck(CreateWeatherChecker.class)` - Direct class reference in annotation
- IDE navigation from annotation to checker class
- Compile-time safety with type checking
- No string-based lookups or reflection for handler discovery

### 2. **Self-Contained Checkers**
- Each checker class contains its own validation logic
- Messages defined in centralized enum
- No external configuration needed
- Easy to understand and maintain

### 3. **Clean Inheritance Hierarchy**
```
BaseChecker → WeatherChecker → CreateWeatherChecker
                             → UpdateWeatherChecker
                             → DeleteWeatherChecker
```
- BaseChecker: Generic JPA utilities
- WeatherChecker: Entity-specific repository access
- Specific checkers: Operation-specific validation

### 4. **Standard Exception Handling**
- Uses `IllegalStateException` and `IllegalArgumentException`
- No custom exception classes needed
- GlobalExceptionHandler maps to HTTP status codes
- Follows Java best practices

### 5. **Spring Integration**
- Checkers are Spring beans (`@Component`)
- PreCheckAspect retrieves beans from ApplicationContext
- Automatic dependency injection for repositories
- Leverages Spring AOP for method interception

### 6. **Extensibility**
- Add new entity: Create new XxxChecker extending BaseChecker
- Add new operation: Create new checker class
- No changes to foundation layer required
- Framework scales cleanly

### 7. **Separation of Concerns**
- **Foundation**: Generic `@PreCheck`, `PreCheckHandler`, `PreCheckAspect`
- **Entity Base**: `WeatherChecker` with repository access
- **Operation Specific**: `CreateWeatherChecker`, `UpdateWeatherChecker`, etc.
- **Messages**: `WeatherCheckMessageEnum` for all error messages

## Summary

The PreCheck framework provides:

### **For Developers:**
- ✅ **Type-safe annotations** with class references
- ✅ **Self-contained checkers** with clear responsibilities
- ✅ **Clean inheritance** hierarchy
- ✅ **Standard exceptions** - no custom exception classes
- ✅ **Easy extensibility** - add new checkers without modifying framework
- ✅ **IDE-friendly** - navigate from annotation to checker class

### **For Users:**
- ✅ **Consistent error messages** with clear context
- ✅ **Proper HTTP status codes** (409 for duplicates, 404 for not found)
- ✅ **Actionable error messages** explaining what went wrong

### **Architecture Highlights:**
- Class-based annotation design for type safety
- Simple `doCheck(Object arg)` interface
- Three-level inheritance: BaseChecker → EntityChecker → OperationChecker
- Standard Java exceptions handled by GlobalExceptionHandler
- Spring-managed beans with automatic discovery
- No reflection needed for handler selection

This gives you a **clean, maintainable validation framework** that's easy to extend and understand.

