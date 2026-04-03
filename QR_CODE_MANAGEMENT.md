# QR Code Management System

A complete QR code generation and management system with URL shortening and redirection capabilities.

## Features

### 1. **Create QR Codes with URLs**
- Users can upload a URL (ASCII only, maximum 20 characters)
- System generates a unique short code for the URL
- System creates a QR code image containing a redirect URL
- QR code is saved to the database with user information

### 2. **Manage QR Codes**
Users can perform CRUD operations on their QR codes:
- **List**: Get all QR codes created by a user
- **View**: Get details of a specific QR code (including scan statistics)
- **Delete**: Remove a QR code from the system

### 3. **QR Code Scanning & Redirection**
- When a user scans the QR code, they are automatically redirected to the original URL
- System tracks scan count and last scanned timestamp
- Redirect endpoint: `/api/qrcode/r/{shortCode}`

## API Endpoints

### Create QR Code
```http
POST /api/qrcode/create
Content-Type: application/json

{
  "url": "https://abc.com",
  "userId": "user123",
  "width": 300,
  "height": 300
}
```

**URL Requirements:**
- Must start with `http://` or `https://`
- ASCII characters only
- Maximum 20 characters
- Width/Height: 100-1000 pixels (optional, default: 300)

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "QR code created successfully",
  "data": {
    "id": 1,
    "shortCode": "abc123",
    "originalUrl": "https://abc.com",
    "qrCodeImage": "base64-encoded-image...",
    "userId": "user123",
    "width": 300,
    "height": 300,
    "scanCount": 0,
    "createdAt": "2026-04-03T10:00:00",
    "redirectUrl": "http://localhost:8080/api/qrcode/r/abc123"
  }
}
```

### Get User's QR Codes
```http
GET /api/qrcode/user/{userId}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "QR codes retrieved successfully",
  "data": [
    {
      "id": 1,
      "shortCode": "abc123",
      "originalUrl": "https://abc.com",
      "qrCodeImage": "base64-encoded-image...",
      "scanCount": 5,
      "createdAt": "2026-04-03T10:00:00",
      "lastScannedAt": "2026-04-03T12:00:00",
      "redirectUrl": "http://localhost:8080/api/qrcode/r/abc123"
    }
  ]
}
```

### Get QR Code Details
```http
GET /api/qrcode/detail/{shortCode}?userId={userId}
```

### Delete QR Code
```http
DELETE /api/qrcode/delete/{shortCode}?userId={userId}
```

### QR Code Redirect (Scanning)
```http
GET /api/qrcode/r/{shortCode}
```

This endpoint is embedded in the QR code. When scanned, it:
1. Looks up the original URL by short code
2. Increments the scan count
3. Updates the last scanned timestamp
4. Redirects (HTTP 302) to the original URL

## Database Schema

```sql
CREATE TABLE qr_codes (
    id BIGSERIAL PRIMARY KEY,
    short_code VARCHAR(10) NOT NULL UNIQUE,
    original_url VARCHAR(20) NOT NULL,
    user_id VARCHAR(50),
    width INTEGER,
    height INTEGER,
    scan_count BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    last_scanned_at TIMESTAMP
);
```

## Validation

### Request Validation (QRCodeRequest DTOs)
- URL validation with `@Pattern`, `@Size`, `@NotBlank`, and custom `@Ascii` annotation
- Width/Height validation with `@Min(100)` and `@Max(1000)`

### Business Logic Validation (QRCodeChecker)
- Duplicate QR code detection (5-minute window)
- In-memory cache to prevent rapid duplicate creation
- Automatic cache cleanup

## Security Considerations

1. **User Authorization**: All management endpoints require a `userId` parameter
2. **ASCII Validation**: URLs are restricted to ASCII characters to prevent encoding issues
3. **Length Limitation**: 20-character limit prevents abuse and ensures QR code readability
4. **Duplicate Detection**: Prevents spam by tracking recent QR code generation

## Implementation Details

### Components

1. **Entity**: `QRCode.java` - JPA entity for database persistence
2. **Repository**: `QRCodeRepository.java` - Data access layer
3. **Service**: `QRCodeService.java` - Business logic
4. **Controller**: `QRCodeController.java` - REST API endpoints
5. **DTOs**: 
   - `CreateQRCodeRequest` - Request for creating QR codes
   - `QRCodeDetailResponse` - Response with QR code details
6. **Validator**: `@Ascii` annotation and `AsciiValidator` for custom validation

### QR Code Generation Process

1. User submits URL via `POST /api/qrcode/create`
2. Request validation (annotations)
3. Business validation (QRCodeChecker)
4. Generate unique 6-character short code
5. Create redirect URL: `{baseUrl}/api/qrcode/r/{shortCode}`
6. Generate QR code image containing the redirect URL
7. Save QR code mapping to database
8. Return QR code image and details to user

### Scanning Flow

1. User scans QR code with mobile device
2. QR reader extracts URL: `{baseUrl}/api/qrcode/r/{shortCode}`
3. Device navigates to the redirect endpoint
4. Server looks up original URL by short code
5. Server increments scan count
6. Server returns HTTP 302 redirect to original URL
7. Device follows redirect to original URL

## Configuration

Add the following to `application.properties`:

```properties
# Base URL for QR code redirects
app.base-url=http://localhost:8080

# Database configuration (if not already set)
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password

# JPA configuration
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
```

## Usage Examples

### Example 1: Create a QR Code
```bash
curl -X POST http://localhost:8080/api/qrcode/create \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://google.com",
    "userId": "user123",
    "width": 300,
    "height": 300
  }'
```

### Example 2: List User's QR Codes
```bash
curl http://localhost:8080/api/qrcode/user/user123
```

### Example 3: Delete a QR Code
```bash
curl -X DELETE "http://localhost:8080/api/qrcode/delete/abc123?userId=user123"
```

## Testing

You can test the QR code system using the Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

Navigate to the "QR Code" section to test all endpoints interactively.

## Future Enhancements

1. **Custom short codes**: Allow users to specify their own short codes
2. **QR code analytics**: Track scan locations, devices, timestamps
3. **Expiration dates**: Auto-expire QR codes after a certain period
4. **Batch creation**: Create multiple QR codes at once
5. **Export formats**: Support SVG, JPG, PDF formats
6. **Custom styling**: Allow customization of QR code colors and logos
7. **API rate limiting**: Prevent abuse with rate limits
8. **OAuth integration**: Use real authentication instead of userId parameter

## Troubleshooting

### Common Issues

**Issue**: QR code not redirecting
- Check that the database contains the short code
- Verify the base URL configuration is correct
- Ensure the redirect endpoint is accessible

**Issue**: URL validation failing
- Ensure URL starts with http:// or https://
- Check that URL is 20 characters or less
- Verify URL contains only ASCII characters

**Issue**: Duplicate QR code error
- Wait 5 minutes before generating the same URL again
- Or use the service's clearCache() method for testing
