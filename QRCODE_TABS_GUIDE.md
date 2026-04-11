# QR Code Web Interface - Which Tab to Use?

## Understanding the Two Tabs

### ❌ Tab 1: "Simple Generator" - NO DATABASE STORAGE

**What it does:**
- Generates QR codes instantly
- Returns Base64 image for download
- **Does NOT save anything to database**
- Perfect for one-time QR codes

**API Called:** `/api/qrcode/generate/base64`

**Use this when:**
- You just need a quick QR code
- Don't need tracking or management
- Don't care about URL shortening

---

### ✅ Tab 2: "URL Manager" - SAVES TO DATABASE

**What it does:**
- Creates QR code with URL shortening
- Generates unique **shortCode** (e.g., "abc123")
- **Saves to database**: shortCode, originalUrl, userId, dimensions
- Provides trackable redirect URL
- Shows scan count and statistics

**API Called:** `/api/qrcode/create`

**Use this when:**
- You want to save QR codes for later
- Need URL shortening (max 20 char URLs)
- Want to track scans
- Need to manage/delete QR codes

---

## Step-by-Step: How to Save to Database

### 1. Start the Application
```bash
cd demo
./mvnw spring-boot:run
```

### 2. Open the Web Interface
```
http://localhost:8081/qrcode/ui
```

### 3. Enter User ID (Required)
At the top of the page, enter your user ID:
```
User ID: testuser123
```

### 4. Switch to "URL Manager" Tab
Click the **second tab** with the 📊 icon that says "URL Manager"

### 5. Fill in the Form
```
URL to Shorten: https://abc.com
Width: 300
Height: 300
```

**Important:** URL must be:
- Max 20 characters
- ASCII only
- Start with http:// or https://

### 6. Click "Create & Shorten URL"
This will save to the database!

### 7. Verify in "My QR Codes" Tab
Click the third tab (📋 My QR Codes) and click "Refresh List"

You should see:
- Your QR code
- Short code (e.g., "abc123")
- Original URL
- Redirect URL
- Scan count

### 8. Verify in Database
Connect to PostgreSQL and check:
```sql
SELECT 
    id,
    short_code,
    original_url,
    user_id,
    width,
    height,
    scan_count,
    created_at
FROM qr_codes
ORDER BY created_at DESC;
```

---

## Common Mistakes

### ❌ WRONG: Using "Simple Generator" Tab
```
Tab: "Simple Generator" ← This does NOT save
Button: "Generate QR Code"
API: /api/qrcode/generate/base64
Result: No database entry
```

### ✅ CORRECT: Using "URL Manager" Tab
```
Tab: "URL Manager" ← This SAVES to database
Button: "Create & Shorten URL"
API: /api/qrcode/create
Result: Saved with shortCode
```

---

## API Endpoints Comparison

| Endpoint | Saves to DB? | Returns | Use Case |
|----------|-------------|---------|----------|
| `/api/qrcode/generate/base64` | ❌ No | Base64 image only | Quick generation |
| `/api/qrcode/create` | ✅ Yes | Image + shortCode + redirectUrl | Managed QR codes |

---

## Debugging

If QR codes still aren't saving after using the correct tab:

1. **Check application logs**:
   ```bash
   # Look for SQL INSERT statements
   # Should see: "Hibernate: insert into qr_codes..."
   ```

2. **Check for errors in browser console**:
   - Open DevTools (F12)
   - Go to Console tab
   - Try creating QR code
   - Look for red error messages

3. **Verify database connection**:
   ```sql
   -- Check table exists
   \dt qr_codes
   
   -- Check if any data exists
   SELECT COUNT(*) FROM qr_codes;
   ```

4. **Check User ID is provided**:
   - The userId field at the top must be filled in
   - It's required for the URL Manager feature

---

## Summary

**To save QR codes to database:**
1. ✅ Use **"URL Manager"** tab (second tab)
2. ✅ Enter **User ID** at top
3. ✅ Enter URL (max 20 chars)
4. ✅ Click **"Create & Shorten URL"**
5. ✅ Check **"My QR Codes"** tab to verify

**Don't use:**
- ❌ "Simple Generator" tab - This doesn't save anything
