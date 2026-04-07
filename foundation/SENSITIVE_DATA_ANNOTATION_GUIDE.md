# Sensitive Data Annotation Usage Guide

## Overview

The `@SensitiveData` annotation allows you to mark fields in your request/response objects as sensitive, enabling automatic masking during serialization, logging, or display operations.

## Quick Start

### 1. Annotate Your Fields

```java
import com.example.foundation.annotation.SensitiveData;
import com.example.foundation.enums.SensitiveType;

public class UserRequest {
    
    @SensitiveData(type = SensitiveType.PHONE)
    private String phoneNumber;
    
    @SensitiveData(type = SensitiveType.EMAIL)
    private String email;
    
    @SensitiveData(type = SensitiveType.ID_NUMBER)
    private String idNumber;
    
    // Non-sensitive fields don't need annotation
    private String username;
    
    // Getters and setters...
}
```

### 2. Process and Mask Data

```java
import com.example.foundation.util.SensitiveDataProcessor;

// Create your object
UserRequest request = new UserRequest();
request.setPhoneNumber("0912345678");
request.setEmail("user@example.com");
request.setIdNumber("A123456789");
request.setUsername("johndoe");

// Get masked values as a map
Map<String, Object> masked = SensitiveDataProcessor.maskSensitiveFields(request);
// Result: {
//   phoneNumber: "091***5678",
//   email: "u***@example.com", 
//   idNumber: "A12****789",
//   username: "johndoe"
// }

// Get masked string for logging
String maskedString = SensitiveDataProcessor.toMaskedString(request);
// Result: "UserRequest{phoneNumber=091***5678, email=u***@example.com, idNumber=A12****789, username=johndoe}"
```

## Available Sensitive Types

### Personal Information

| Type | Example Input | Example Output | Description |
|------|--------------|----------------|-------------|
| `PHONE` | 0912345678 | 091\*\*\*5678 | Phone number (keep first 3, last 4) |
| `EMAIL` | user@example.com | u\*\*\*@example.com | Email (mask username) |
| `EMAIL_STRICT` | user@example.com | u\*\*\*@e\*\*\*\*\*\*.com | Email (mask username & domain) |
| `NAME` | John Doe | J\*\*\* D\*\* | Name (keep first char) |
| `CHINESE_NAME` | 王小明 | 王\*\* | Chinese name (keep first char) |
| `ID_NUMBER` | A123456789 | A12\*\*\*\*789 | ID number (keep first 3, last 3) |
| `ADDRESS` | 台北市信義區忠孝東路100號 | 台北市信義區\*\*\*\*\*\*\*\* | Address (keep first 6 chars) |

### Financial Information

| Type | Example Input | Example Output | Description |
|------|--------------|----------------|-------------|
| `CREDIT_CARD` | 1234567890123456 | \*\*\*\*\*\*\*\*\*\*\*\*3456 | Card number (last 4 only) |
| `CREDIT_CARD_FORMATTED` | 1234567890123456 | xxxx-xxxx-xxxx-3456 | Card number (formatted) |
| `BANK_ACCOUNT` | 1234567890123 | \*\*\*\*\*\*\*\*\*0123 | Bank account (last 4 only) |
| `BANK_ACCOUNT_PARTIAL` | 1234567890123 | 123\*\*\*\*\*\*0123 | Bank account (partial) |

### Security Credentials

| Type | Example Input | Example Output | Description |
|------|--------------|----------------|-------------|
| `PASSWORD` | password123 | \*\*\*\*\*\*\*\* | Password (fixed 8 asterisks) |
| `PASSWORD_WITH_LENGTH` | password123 | \*\*\*\*\*\*\*\*\*\*\* | Password (actual length) |

### General/Custom

| Type | Example Input | Example Output | Description |
|------|--------------|----------------|-------------|
| `DEFAULT` | A123456789 | A12\*\*\*\*789 | Default (keep first 3, last 3) |
| `CUSTOM` | *(see below)* | *(configurable)* | Custom masking parameters |

## Custom Masking

For flexible masking, use `SensitiveType.CUSTOM` with custom parameters:

```java
public class CustomRequest {
    
    // Keep first 2 and last 2 characters
    @SensitiveData(
        type = SensitiveType.CUSTOM, 
        keepStart = 2, 
        keepEnd = 2
    )
    private String customField;  // "ABCDEFGH" -> "AB****GH"
    
    // Use custom mask character
    @SensitiveData(
        type = SensitiveType.CUSTOM, 
        keepStart = 1, 
        keepEnd = 4,
        maskChar = "#"
    )
    private String customField2;  // "1234567890" -> "1#####7890"
}
```

## Advanced Usage

### Conditional Masking

You can temporarily disable masking using the `enabled` parameter:

```java
public class DebugRequest {
    @SensitiveData(
        type = SensitiveType.PHONE, 
        enabled = false  // Disable for debugging
    )
    private String phoneNumber;
}
```

### Field Documentation

Add descriptions for documentation purposes:

```java
public class DocumentedRequest {
    @SensitiveData(
        type = SensitiveType.EMAIL,
        description = "User's primary contact email"
    )
    private String email;
}
```

### Check if Field is Sensitive

```java
import java.lang.reflect.Field;

Field field = UserRequest.class.getDeclaredField("phoneNumber");
boolean isSensitive = SensitiveDataProcessor.isSensitiveField(field);
// Result: true
```

### Mask Specific Field

```java
UserRequest request = new UserRequest();
request.setPhoneNumber("0912345678");

String masked = SensitiveDataProcessor.maskField(request, "phoneNumber");
// Result: "091***5678"
```

### Check if Object Has Sensitive Fields

```java
UserRequest request = new UserRequest();
boolean hasSensitive = SensitiveDataProcessor.hasSensitiveFields(request);
// Result: true
```

## Real-World Examples

### API Request Logging

```java
@RestController
public class UserController {
    
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody UserRequest request) {
        // Log request with masked sensitive data
        log.info("Creating user: {}", SensitiveDataProcessor.toMaskedString(request));
        
        // Process request...
        return ResponseEntity.ok(user);
    }
}
```

### Payment Processing

```java
public class PaymentRequest {
    @SensitiveData(type = SensitiveType.CREDIT_CARD)
    private String cardNumber;
    
    @SensitiveData(type = SensitiveType.PASSWORD)
    private String cvv;
    
    @SensitiveData(type = SensitiveType.NAME)
    private String cardHolderName;
    
    private BigDecimal amount;
    
    // When logging or auditing
    public String toMaskedString() {
        return SensitiveDataProcessor.toMaskedString(this);
    }
}
```

### User Profile Response

```java
public class UserProfileResponse {
    private Long userId;
    
    @SensitiveData(type = SensitiveType.EMAIL)
    private String email;
    
    @SensitiveData(type = SensitiveType.PHONE)
    private String phoneNumber;
    
    @SensitiveData(type = SensitiveType.ADDRESS)
    private String address;
    
    private String displayName; // Public field
}
```

### Audit Trail

```java
public class AuditService {
    
    public void auditRequest(Object request) {
        if (SensitiveDataProcessor.hasSensitiveFields(request)) {
            // Log with masked sensitive fields
            String maskedData = SensitiveDataProcessor.toMaskedString(request);
            auditLog.info("Request: {}", maskedData);
        } else {
            // Log normally
            auditLog.info("Request: {}", request);
        }
    }
}
```

## Best Practices

1. **Always annotate sensitive fields**: Mark all PII (Personally Identifiable Information) and financial data
2. **Choose appropriate types**: Select the masking strategy that matches your data type
3. **Use for logging**: Always mask sensitive data before logging to prevent data leaks
4. **Audit trail**: Include masked data in audit trails, not raw values
5. **Debug mode**: Use `enabled = false` during development, but never in production
6. **Documentation**: Add descriptions to help other developers understand the sensitivity
7. **Test thoroughly**: Verify masking works correctly with your data patterns

## Integration with JSON Serialization

To integrate with Jackson for automatic masking during JSON serialization, you can create a custom serializer:

```java
public class SensitiveDataSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) 
            throws IOException {
        // Get field annotation and mask accordingly
        // Implementation depends on your Jackson setup
        gen.writeString(maskedValue);
    }
}
```

## Notes

- The annotation is marked with `@Retention(RUNTIME)`, so it's available for reflection at runtime
- Fields marked with `@SensitiveData` have their values masked but the original object remains unchanged
- Masking is performed by `MarkSensitiveDataUtil` utility methods
- All masking operations are null-safe and handle edge cases
