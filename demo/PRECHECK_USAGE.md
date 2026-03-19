# @PreCheck Annotation - Quick Reference

## Usage Example

Use the `@PreCheck` annotation with checker class references for automatic validation:

```java
// ✅ RECOMMENDED - Class-based annotation
@PreCheck(CreateWeatherChecker.class)
public BaseResponse<WeatherResponse> create(CreateWeatherArg arg) {
    // Your business logic here - validation happens automatically via AOP
    Weather weather = new Weather();
    weather.setCity(arg.getCity());
    weather.setTempLo(arg.getTempLo());
    weather.setTempHi(arg.getTempHi());
    weather.setPrcp(arg.getPrcp());
    weather.setDate(arg.getDate());
    
    Weather savedWeather = weatherRepository.save(weather);
    WeatherResponse response = WeatherResponse.fromEntity(savedWeather);
    return BaseResponse.success("Weather record created successfully", response);
}

// ❌ OLD WAY - Manual checker calls (no longer needed)
public BaseResponse<WeatherResponse> create(CreateWeatherArg arg) {
    // Manual validation - error prone and verbose
    if (weatherRepository.findByCityAndDate(arg.getCity(), arg.getDate()) != null) {
        throw new IllegalStateException("Weather data already exists");
    }
    // business logic
}
```

**Key Benefits:**
- ✅ Clean, declarative code
- ✅ Type-safe class references
- ✅ IDE navigation support (click on checker class to view)
- ✅ Compile-time verification
- ✅ AOP handles validation transparently

## Complete WeatherService Example

```java
@Service
public class WeatherService {

    @Autowired
    private WeatherRepository weatherRepository;

    // CREATE - Check for duplicates
    @PreCheck(CreateWeatherChecker.class)
    public BaseResponse<WeatherResponse> create(CreateWeatherArg arg) {
        Weather weather = new Weather();
        weather.setCity(arg.getCity());
        weather.setTempLo(arg.getTempLo());
        weather.setTempHi(arg.getTempHi());
        weather.setPrcp(arg.getPrcp());
        weather.setDate(arg.getDate());
        
        Weather savedWeather = weatherRepository.save(weather);
        WeatherResponse response = WeatherResponse.fromEntity(savedWeather);
        return BaseResponse.success("Weather record created successfully", response);
    }

    // UPDATE - Check if data exists
    @PreCheck(UpdateWeatherChecker.class)
    public BaseResponse<WeatherResponse> update(UpdateWeatherArg arg) {
        Weather weather = weatherRepository.findByCityAndDate(arg.getCity(), arg.getDate());
        
        weather.setTempLo(arg.getTempLo());
        weather.setTempHi(arg.getTempHi());
        weather.setPrcp(arg.getPrcp());

        Weather updatedWeather = weatherRepository.save(weather);
        WeatherResponse response = WeatherResponse.fromEntity(updatedWeather);
        return BaseResponse.success("Weather record updated successfully", response);
    }

    // DELETE - Check if data exists
    @PreCheck(DeleteWeatherChecker.class)
    public BaseResponse<Void> delete(DeleteWeatherArg arg) {
        weatherRepository.deleteByCityAndDate(arg.getCity(), arg.getDate());
        return BaseResponse.success("Weather record(s) deleted successfully", null);
    }
}
```

## How It Works

```
┌──────────────────────────────────────────────────────────────┐
│  1. Service Method Call                                      │
│     @PreCheck(CreateWeatherChecker.class)                    │
│     public BaseResponse<WeatherResponse> create(...)         │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────────────┐
│  2. PreCheckAspect Intercepts                                │
│     - Gets CreateWeatherChecker.class from annotation        │
│     - Retrieves bean from Spring ApplicationContext          │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────────────┐
│  3. CreateWeatherChecker.doCheck(arg)                        │
│     - Validates no duplicate exists                          │
│     - Throws IllegalStateException if duplicate found        │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────────────┐
│  4. If Valid: Service Method Executes                        │
│     If Invalid: GlobalExceptionHandler converts to HTTP      │
└──────────────────────────────────────────────────────────────┘
```

## Checker Implementation Pattern

### Entity Base Checker

```java
@Component
public abstract class WeatherChecker extends BaseChecker {
    
    @Autowired
    protected WeatherRepository weatherRepository;

    protected boolean isWeatherExist(String city, LocalDate date) {
        Weather weather = weatherRepository.findByCityAndDate(city, date);
        return weather != null;
    }
}
```

### Operation-Specific Checker

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

### Message Enum

```java
public enum WeatherCheckMessageEnum {
    
    CREATE_DUPLICATE("Weather data already exists for {city} on {date}"),
    UPDATE_NOT_FOUND("No weather data found for {city} on {date}. Unable to update."),
    DELETE_NOT_FOUND("No weather data found for {city} on {date}. Unable to delete.");
    
    private final String messageTemplate;
    
    WeatherCheckMessageEnum(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }
    
    public String getMessage(String city, String date) {
        return messageTemplate
                .replace("{city}", city != null ? city : "null")
                .replace("{date}", date != null ? date : "null");
    }
}
```

## Error Responses

### Duplicate Found (CREATE)
```json
{
  "success": false,
  "message": "Weather data already exists for Hong Kong on 2026-03-15",
  "data": null
}
```
**HTTP Status:** 409 Conflict

### Data Not Found (UPDATE/DELETE)
```json
{
  "success": false,
  "message": "No weather data found for Tokyo on 2026-01-01. Unable to update.",
  "data": null
}
```
**HTTP Status:** 404 Not Found

## Adding New Entity Checkers

### Step 1: Create Entity Base Checker

```java
package com.example.demo.service.user.checker;

@Component
public abstract class UserChecker extends BaseChecker {
    
    @Autowired
    protected UserRepository userRepository;

    protected boolean isUserExist(Long id) {
        return userRepository.findById(id).isPresent();
    }
    
    protected boolean isEmailExist(String email) {
        return userRepository.findByEmail(email) != null;
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
    
    public String getMessage(String value) {
        return messageTemplate
                .replace("{email}", value)
                .replace("{id}", value);
    }
}
```

### Step 3: Create Operation Checkers

```java
@Component
public class CreateUserChecker extends UserChecker {
    
    @Override
    public void doCheck(Object arg) {
        if (!(arg instanceof CreateUserArg)) {
            throw new IllegalArgumentException("CreateUserChecker requires CreateUserArg");
        }
        
        CreateUserArg createArg = (CreateUserArg) arg;
        
        if (isEmailExist(createArg.getEmail())) {
            String errorMessage = UserCheckMessageEnum.CREATE_DUPLICATE
                .getMessage(createArg.getEmail());
            throw new IllegalStateException(errorMessage);
        }
    }
}

@Component
public class UpdateUserChecker extends UserChecker {
    
    @Override
    public void doCheck(Object arg) {
        if (!(arg instanceof UpdateUserArg)) {
            throw new IllegalArgumentException("UpdateUserChecker requires UpdateUserArg");
        }
        
        UpdateUserArg updateArg = (UpdateUserArg) arg;
        
        if (!isUserExist(updateArg.getId())) {
            String errorMessage = UserCheckMessageEnum.UPDATE_NOT_FOUND
                .getMessage(updateArg.getId().toString());
            throw new IllegalArgumentException(errorMessage);
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
        // Validation happens automatically
        User user = new User();
        user.setEmail(arg.getEmail());
        user.setName(arg.getName());
        return userRepository.save(user);
    }
    
    @PreCheck(UpdateUserChecker.class)
    public BaseResponse<UserResponse> update(UpdateUserArg arg) {
        // Validation happens automatically
        User user = userRepository.findById(arg.getId()).get();
        user.setName(arg.getName());
        return userRepository.save(user);
    }
    
    @PreCheck(DeleteUserChecker.class)
    public BaseResponse<Void> delete(DeleteUserArg arg) {
        // Validation happens automatically
        userRepository.deleteById(arg.getId());
        return BaseResponse.success("User deleted successfully");
    }
}
```

## Best Practices

### ✅ DO
- Use class-based `@PreCheck` annotations
- Create separate checker classes for each operation (Create/Update/Delete)
- Centralize error messages in enum classes
- Throw `IllegalStateException` for duplicates (CREATE)
- Throw `IllegalArgumentException` for not found (UPDATE/DELETE)
- Type check arguments early in `doCheck()`

### ❌ DON'T
- Don't put all validation in one checker class
- Don't hardcode error messages in checker classes
- Don't manually call checker methods - use `@PreCheck` annotation
- Don't use custom exception classes - use standard Java exceptions
- Don't skip type checking in `doCheck()` method

## Summary

The `@PreCheck` framework provides:
- **Class-based annotations** for type safety and IDE support
- **Self-contained checkers** with clear responsibilities
- **Automatic validation** via Spring AOP
- **Standard exception handling** with proper HTTP status codes
- **Easy extensibility** for new entities and operations

For complete architecture details, see [PRECHECK_GUIDE.md](PRECHECK_GUIDE.md).
