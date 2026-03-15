# @PreCheck Annotation - Quick Reference

## Usage Example

Use the `@PreCheck` annotation with centralized message enums for better management:

```java
// ✅ RECOMMENDED - Enum-based messages (centralized management)
@PreCheck(value = CheckType.CREATE, message = WeatherCheckMessage.CREATE_DUPLICATE)
public BaseResponse<WeatherResponse> create(CreateWeatherArg arg) {
    // Your business logic here
    Weather weather = new Weather();
    weather.setCity(arg.getCity());
    // ... rest of the code
}

// ❌ OLD WAY - Manual checker calls (no longer needed)
public BaseResponse<WeatherResponse> create(CreateWeatherArg arg) {
    weatherChecker.CreateWeatherChecker(arg, 
        String.format("Weather data already exists for %s on %s", arg.getCity(), arg.getDate()));
    // business logic
}
```

## WeatherCheckMessage Enum

All error messages are centralized in the `WeatherCheckMessage` enum:

```java
public enum WeatherCheckMessage {
    CREATE_DUPLICATE("Weather data already exists for {city} on {date}"),
    UPDATE_NOT_FOUND("No weather data found for {city} on {date}. Unable to update."),
    DELETE_NOT_FOUND("No weather data found for {city} on {date}. Unable to delete.");
    
    // Messages support placeholders: {city}, {date}
}
```

**Benefits:**
- ✅ Centralized message management
- ✅ Easy to update messages in one place
- ✅ IntelliSense support in IDE
- ✅ No typos in message strings
- ✅ Reusable across multiple services

## Complete WeatherService Example

```java
import com.example.demo.service.weather.checker.WeatherCheckMessage;

@Service
public class WeatherService {

    @Autowired
    private WeatherRepository weatherRepository;

    // CREATE - Check for duplicates
    @PreCheck(value = CheckType.CREATE, message = WeatherCheckMessage.CREATE_DUPLICATE)
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
    @PreCheck(value = CheckType.UPDATE, message = WeatherCheckMessage.UPDATE_NOT_FOUND)
    public BaseResponse<WeatherResponse> update(UpdateWeatherArg arg) {
        List<Weather> weatherList = weatherRepository.findByCityAndDate(arg.getCity(), arg.getDate());
        
        Weather weather = weatherList.get(0);
        weather.setTempLo(arg.getTempLo());
        weather.setTempHi(arg.getTempHi());
        weather.setPrcp(arg.getPrcp());

        Weather updatedWeather = weatherRepository.save(weather);
        WeatherResponse response = WeatherResponse.fromEntity(updatedWeather);
        return BaseResponse.success("Weather record updated successfully", response);
    }

    // DELETE - Check if data exists
    @PreCheck(value = CheckType.DELETE, message = WeatherCheckMessage.DELETE_NOT_FOUND)
    public BaseResponse<Void> delete(DeleteWeatherArg arg) {
        weatherRepository.deleteByCityAndDate(arg.getCity(), arg.getDate());
        return BaseResponse.success("Weather record(s) deleted successfully", null);
    }
}
```

## How It Works

1. **Annotation on Method**: Add `@PreCheck` to your service method
2. **Specify CheckType**: `CREATE`, `UPDATE`, or `DELETE`
3. **Custom Message**: Define your error message with `{placeholders}`
4. **AOP Magic**: `PreCheckAspect` intercepts the call and validates automatically
5. **WeatherChecker**: The aspect calls the appropriate checker method
6. **Exception Thrown**: If validation fails, exception is thrown with your message

Message templates in the enum support placeholders that are automatically replaced:

```java
public enum WeatherCheckMessage {
    CREATE_DUPLICATE("Weather data already exists for {city} on {date}"),
    // Placeholders {city} and {date} are replaced at runtime
}
```

Available placeholders:
- `{city}` - Replaced with `arg.getCity()`
- `{date}` - Replaced with `arg.getDate()`

Example result: `"Weather data already exists for Hong Kong on 2026-03-15"`

## Adding New Messages

To add a new validation message:

1. **Add to WeatherCheckMessage enum:**
```java
public enum WeatherCheckMessage {
    CREATE_DUPLICATE("Weather data already exists for {city} on {date}"),
    UPDATE_NOT_FOUND("No weather data found for {city} on {date}. Unable to update."),
    DELETE_NOT_FOUND("No weather data found for {city} on {date}. Unable to delete."),
    
    // Add your new message here
    CUSTOM_VALIDATION("Your custom message with {city} and {date}");
    
    private final String messageTemplate;
    // ... rest of enum code
}entralized Messages**: All error messages in one enum for easy management  
✅ **IDE Support**: IntelliSense helps you choose the right message  
✅ **No Typos**: Compile-time checking of message references  
✅ **Consistent**: Same pattern across all CRUD operations  
✅ **DRY**: No repeated validation code  
✅ **Maintainable**: Change message text in one place, affects all usages
```java
@PreCheck(value = CheckType.CREATE, message = WeatherCheckMessage.CUSTOM_VALIDATION)
public BaseResponse<WeatherResponse> yourMethod(CreateWeatherArg arg) {
    // business logic
}
``

Example result: `"Weather data already exists for Hong Kong on 2026-03-15"`

## CheckType Values

```java
public enum CheckType {
    CREATE,   // Throws DuplicateDataException if data exists
    UPDATE,   // Throws DataNotFoundException if data doesn't exist
    DELETE    // Throws DataNotFoundException if data doesn't exist
}
```

## Benefits

✅ **Clean Code**: No manual checker calls cluttering your business logic  
✅ **Declarative**: Annotation clearly shows what validation is happening  
✅ **Consistent**: Same pattern across all CRUD operations  
✅ **DRY**: No repeated validation code  
✅ **Readable**: Error messages defined right at the method level  
✅ **Maintainable**: Change validation logic in one place (PreCheckAspect)

## Response Examples

### Success (200 OK)
```json
{
  "success": true,
  "message": "Weather record created successfully",
  "data": { "id": 1, "city": "Hong Kong", ... }
}
```

### Duplicate Error (409 Conflict)
```json
{
  "success": false,
  "message": "Weather data already exists for Hong Kong on 2026-03-15",
  "data": null
}
```

### Not Found Error (404 Not Found)
```json
{
  "success": false,
  "message": "No weather data found for Tokyo on 2026-01-01. Unable to update.",
  "data": null
}
```

## Architecture

```
@PreCheck Annotation
        ↓
PreCheckAspect (AOP)
        ↓
WeatherChecker
        ↓
WeatherRepository
        ↓
Database Query
        ↓
Exception or Continue
```

## Creating Your Own Checker

For other entities, follow this pattern:

1. Create a checker class in `service/yourentity/checker/`
2. Inject it into `PreCheckAspect`
3. Add case handling in `PreCheckAspect.performPreCheck()`
4. Use `@PreCheck` annotation on your service methods
