# QR Code Web Interface Guide

A modern, responsive web interface for generating and managing QR codes with URL shortening capabilities.

## Features

### 🎨 Simple Generator
- Generate QR codes from any text or URL
- Customize QR code size (100-1000 pixels)
- Download QR codes as PNG images
- Copy Base64-encoded QR codes
- Real-time preview

### 📊 URL Manager
- Create QR codes with automatic URL shortening
- Track QR code scans and statistics
- Generate trackable redirect URLs
- Max 20 characters for shortened URLs
- ASCII-only URL support

### 📋 My QR Codes
- View all your created QR codes
- See scan statistics (total scans, last scanned time)
- Download or delete QR codes
- Copy redirect URLs
- Dashboard with total QR codes and scans

## Accessing the Interface

### Local Development
```
http://localhost:8080/qrcode/ui
```

or simply:
```
http://localhost:8080/qrcode
```

### Home Page
```
http://localhost:8080/
```

## How to Use

### 1. Simple QR Code Generation

1. Go to the **Simple Generator** tab
2. Enter your content (text, URL, etc.)
3. Optionally adjust the size (width/height)
4. Click **Generate QR Code**
5. Download or copy the QR code

**Example Use Cases:**
- WiFi passwords
- Contact information (vCard)
- Product information
- Website URLs
- Plain text messages

### 2. Create Managed QR Code with URL Shortening

1. Enter your **User ID** in the top field
2. Go to the **URL Manager** tab
3. Enter the URL you want to shorten (max 20 characters)
4. Optionally adjust the QR code size
5. Click **Create & Shorten URL**
6. The system will:
   - Generate a unique short code
   - Create a redirect URL
   - Generate a QR code containing the redirect URL
   - Save everything to the database

**The generated QR code will redirect users to your original URL when scanned!**

### 3. Manage Your QR Codes

1. Ensure your **User ID** is entered
2. Go to the **My QR Codes** tab
3. Click **Refresh List** to load your QR codes
4. You can:
   - **Download** QR codes
   - **Copy** redirect URLs
   - **Delete** QR codes
   - View **scan statistics**

## API Endpoints Used

The web interface interacts with these backend APIs:

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/qrcode/generate/base64` | POST | Generate simple QR code |
| `/api/qrcode/create` | POST | Create managed QR code with URL |
| `/api/qrcode/user/{userId}` | GET | Get user's QR codes |
| `/api/qrcode/delete/{shortCode}` | DELETE | Delete QR code |
| `/api/qrcode/r/{shortCode}` | GET | Redirect endpoint (for scanning) |

## URL Restrictions

When using the **URL Manager**:
- URLs must start with `http://` or `https://`
- Maximum 20 characters
- ASCII characters only
- Examples of valid URLs:
  - `https://abc.com` (15 chars)
  - `http://xyz.io` (14 chars)

## QR Code Size Guidelines

- **Minimum**: 100x100 pixels
- **Maximum**: 1000x1000 pixels
- **Recommended**: 300x300 pixels (good balance)
- **For print**: 500x500 pixels or larger

## Browser Support

The interface works in all modern browsers:
- Chrome/Edge (recommended)
- Firefox
- Safari
- Opera

## Screenshots & Usage Tips

### Simple Generator Tab
- Perfect for quick QR code generation
- No database storage
- Immediate download
- Great for one-time use

### URL Manager Tab
- Creates persistent QR codes
- Provides analytics
- Enables URL shortening
- Ideal for marketing campaigns

### My QR Codes Tab
- Central dashboard for all QR codes
- Real-time statistics
- Easy management
- Export capabilities

## Troubleshooting

### QR Code Not Generated
- Check that content is not empty
- Verify size is between 100-1000 pixels
- Check browser console for errors

### URL Manager Fails
- Ensure User ID is provided
- URL must be 20 characters or less
- URL must start with http:// or https://
- Only ASCII characters allowed

### QR Codes Not Loading
- Verify User ID is correct
- Click the Refresh button
- Check network connection
- Verify backend service is running

## Development

### Files
- **HTML Template**: `demo/src/main/resources/templates/qrcode.html`
- **Controller**: `demo/src/main/java/com/example/demo/controller/ui/QRCodeViewController.java`
- **API Controller**: `demo/src/main/java/com/example/demo/controller/qrcode/QRCodeController.java`

### Customization
The interface uses vanilla JavaScript and CSS, making it easy to customize:
- Modify colors in the CSS `:root` variables
- Adjust layouts in the `.form-grid` classes
- Add new features by extending the JavaScript functions

## Related Documentation

- [QR Code API Guide](../QRCODE_API.md)
- [QR Code Management Guide](../QR_CODE_MANAGEMENT.md)
- [Swagger UI](http://localhost:8080/swagger-ui/index.html)

## Support

For issues or questions:
1. Check the API documentation
2. Review browser console for errors
3. Verify backend service status at `/actuator/health`
