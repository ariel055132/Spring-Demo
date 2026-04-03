# QR Code Generator API

A RESTful API for generating QR codes using the ZXing library.

## Features

- **Generate QR Code Images** - Create QR codes in PNG format
- **Base64 Encoding** - Get QR codes as Base64 strings for easy embedding
- **Customizable Size** - Configure width and height (100-1000 pixels)
- **High Error Correction** - Uses Level H error correction for maximum resilience
- **UTF-8 Support** - Encode international characters

## API Endpoints

### 1. Generate QR Code (POST - Image)

**Endpoint:** `POST /api/qrcode/generate`

**Request Body:**
```json
{
  "content": "Hello, QR Code!",
  "width": 300,
  "height": 300
}
```

**Response:** PNG image (Content-Type: `image/png`)

**Example:**
```bash
curl -X POST http://localhost:8081/api/qrcode/generate \
  -H "Content-Type: application/json" \
  -d '{"content": "https://example.com", "width": 300, "height": 300}' \
  --output qrcode.png
```

### 2. Generate QR Code (GET - Image)

**Endpoint:** `GET /api/qrcode/generate`

**Query Parameters:**
- `content` (required) - Content to encode
- `width` (optional, default: 300) - Width in pixels (100-1000)
- `height` (optional, default: 300) - Height in pixels (100-1000)

**Response:** PNG image

**Example:**
```bash
# Simple URL encoding
curl "http://localhost:8081/api/qrcode/generate?content=Hello%20World" --output qrcode.png

# With custom size
curl "http://localhost:8081/api/qrcode/generate?content=https://example.com&width=500&height=500" --output qrcode.png
```

### 3. Generate QR Code as Base64 (POST - JSON)

**Endpoint:** `POST /api/qrcode/generate/base64`

**Request Body:**
```json
{
  "content": "Hello, QR Code!",
  "width": 300,
  "height": 300
}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "QR code generated successfully",
  "data": {
    "qrcode": "iVBORw0KGgoAAAANSUhEUgAA...(Base64 encoded PNG)",
    "format": "PNG",
    "encoding": "Base64",
    "width": 300,
    "height": 300,
    "contentLength": 17
  },
  "timestamp": "2026-04-02T15:30:45.123"
}
```

**Example:**
```bash
curl -X POST http://localhost:8081/api/qrcode/generate/base64 \
  -H "Content-Type: application/json" \
  -d '{
    "content": "https://example.com",
    "width": 400,
    "height": 400
  }'
```

### 4. Health Check

**Endpoint:** `GET /api/qrcode/health`

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "QR code service is operational",
  "data": {
    "service": "QR Code Generator",
    "status": "UP",
    "library": "ZXing 3.5.3",
    "timestamp": "2026-04-02T15:30:00.000"
  },
  "timestamp": "2026-04-02T15:30:00.000"
}
```

## Request Parameters

### QRCodeRequest

| Field | Type | Required | Description | Constraints |
|-------|------|----------|-------------|-------------|
| `content` | String | Yes | Text/URL to encode | Max 4296 characters |
| `width` | Integer | No | Image width in pixels | 100-1000 (default: 300) |
| `height` | Integer | No | Image height in pixels | 100-1000 (default: 300) |

## Use Cases

### 1. Website URL

```bash
curl -X POST http://localhost:8081/api/qrcode/generate \
  -H "Content-Type: application/json" \
  -d '{"content": "https://www.example.com"}' \
  --output website-qr.png
```

### 2. Contact Information (vCard)

```bash
curl -X POST http://localhost:8081/api/qrcode/generate \
  -H "Content-Type: application/json" \
  -d '{
    "content": "BEGIN:VCARD\nVERSION:3.0\nFN:John Doe\nTEL:+1234567890\nEMAIL:john@example.com\nEND:VCARD",
    "width": 400,
    "height": 400
  }' \
  --output contact-qr.png
```

### 3. WiFi Configuration

```bash
curl -X POST http://localhost:8081/api/qrcode/generate \
  -H "Content-Type: application/json" \
  -d '{
    "content": "WIFI:T:WPA;S:MyNetwork;P:MyPassword;;",
    "width": 350,
    "height": 350
  }' \
  --output wifi-qr.png
```

### 4. Plain Text

```bash
curl -X POST http://localhost:8081/api/qrcode/generate \
  -H "Content-Type: application/json" \
  -d '{"content": "Hello, World!"}' \
  --output text-qr.png
```

### 5. Embed in HTML (Base64)

```bash
# Get Base64 encoded QR code
curl -X POST http://localhost:8081/api/qrcode/generate/base64 \
  -H "Content-Type: application/json" \
  -d '{"content": "https://example.com"}' > response.json

# Extract base64 data and use in HTML:
# <img src="data:image/png;base64,{qrcode}" alt="QR Code">
```

## Error Handling

### 400 Bad Request

**Invalid content (empty):**
```json
{
  "status": "ERROR",
  "message": "Content cannot be null or empty",
  "data": null,
  "timestamp": "2026-04-02T15:30:00.000"
}
```

**Content too long:**
```json
{
  "status": "ERROR",
  "message": "Content exceeds maximum length of 4296 characters",
  "data": null,
  "timestamp": "2026-04-02T15:30:00.000"
}
```

**Invalid dimensions:**
```json
{
  "status": "ERROR",
  "message": "Width must be between 100 and 1000 pixels",
  "data": null,
  "timestamp": "2026-04-02T15:30:00.000"
}
```

### 500 Internal Server Error

```json
{
  "status": "ERROR",
  "message": "Failed to generate QR code: encoding error",
  "data": null,
  "timestamp": "2026-04-02T15:30:00.000"
}
```

## QR Code Specifications

- **Format:** PNG image
- **Error Correction:** Level H (30% recovery capability)
- **Character Encoding:** UTF-8
- **Margin:** 1 module (minimal white space around QR code)
- **Max Capacity:** 4,296 alphanumeric characters

## Testing with Swagger UI

Access Swagger UI at: `http://localhost:8081/swagger-ui/index.html`

Navigate to **QR Code** section to:
1. Try out different endpoints interactively
2. View detailed API documentation
3. Test with sample requests

## Integration Examples

### JavaScript / Fetch API

```javascript
// Generate and download QR code
async function generateQRCode(content) {
  const response = await fetch('http://localhost:8081/api/qrcode/generate', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      content: content,
      width: 300,
      height: 300
    })
  });
  
  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  
  // Download
  const a = document.createElement('a');
  a.href = url;
  a.download = 'qrcode.png';
  a.click();
}

// Get Base64 QR code for display
async function getQRCodeBase64(content) {
  const response = await fetch('http://localhost:8081/api/qrcode/generate/base64', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ content })
  });
  
  const data = await response.json();
  
  // Display in img tag
  const img = document.getElementById('qrcode-img');
  img.src = `data:image/png;base64,${data.data.qrcode}`;
}
```

### Python / Requests

```python
import requests

# Generate QR code
response = requests.post(
    'http://localhost:8081/api/qrcode/generate',
    json={
        'content': 'https://example.com',
        'width': 300,
        'height': 300
    }
)

# Save to file
with open('qrcode.png', 'wb') as f:
    f.write(response.content)

# Get Base64
response = requests.post(
    'http://localhost:8081/api/qrcode/generate/base64',
    json={'content': 'Hello World'}
)

data = response.json()
base64_qr = data['data']['qrcode']
```

### Java / RestTemplate

```java
RestTemplate restTemplate = new RestTemplate();

QRCodeRequest request = new QRCodeRequest();
request.setContent("https://example.com");
request.setWidth(300);
request.setHeight(300);

// Get image bytes
byte[] qrCode = restTemplate.postForObject(
    "http://localhost:8081/api/qrcode/generate",
    request,
    byte[].class
);

// Get Base64 response
ResponseEntity<BaseResponse> response = restTemplate.postForEntity(
    "http://localhost:8081/api/qrcode/generate/base64",
    request,
    BaseResponse.class
);
```

## Performance Considerations

- **Generation Time:** ~10-50ms per QR code (depends on size and content length)
- **Memory Usage:** Minimal (< 1MB per request)
- **Concurrent Requests:** Service is stateless and thread-safe
- **Recommended Maximum Size:** 1000x1000 pixels

## Security Notes

⚠️ **Important:**
- Validate and sanitize user input before generating QR codes
- Be cautious with QR codes containing executable content or scripts
- Consider rate limiting for public-facing endpoints
- Don't encode sensitive data without encryption
- Review content for malicious URLs before generating

## Dependencies

- **ZXing Core:** 3.5.3 - QR code encoding/decoding
- **ZXing JavaSE:** 3.5.3 - Image generation support

## Troubleshooting

### QR Code Won't Scan

- **Increase error correction level** (already set to H)
- **Increase size** (try 400x400 or larger)
- **Reduce content length** if very long
- **Check printer quality** if printing physical codes

### Image Quality Issues

- Use larger dimensions (500x500+) for better resolution
- Ensure proper lighting when scanning
- Avoid scaling PNG images (regenerate at target size)

## Future Enhancements

Potential improvements:
- [ ] Support for different image formats (SVG, JPEG, WebP)
- [ ] Custom colors and logos
- [ ] QR code templates (WiFi, vCard, SMS, etc.)
- [ ] Bulk QR code generation
- [ ] QR code scanning/decoding endpoint
- [ ] Analytics and tracking

## References

- [ZXing Documentation](https://github.com/zxing/zxing)
- [QR Code Specification](https://www.qrcode.com/en/about/standards.html)
- [QR Code Data Formats](https://github.com/zxing/zxing/wiki/Barcode-Contents)
