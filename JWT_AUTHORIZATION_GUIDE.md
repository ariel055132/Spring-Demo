# JWT Authorization Guide

This guide explains how to use JWT authentication to access protected APIs through Swagger UI and Postman.

## Overview

The API Gateway requires JWT authentication for most endpoints. Public endpoints (no authentication needed):
- `GET /actuator/health` - Health check
- `GET /api/weather/read` - Read weather data
- `POST /auth/token` - Get JWT token
- `/swagger-ui/**` - Swagger UI pages
- `/v3/api-docs/**` - OpenAPI documentation

All other endpoints require a valid JWT Bearer token.

## Test Users

The gateway has three test users configured:

| Username | Password | Role  |
|----------|----------|-------|
| admin    | password | ADMIN |
| user     | password | USER  |
| test     | password | USER  |

---

## Using Swagger UI

### Step 1: Access Swagger UI

Open your browser and navigate to:
```
http://localhost:8080/swagger-ui/index.html
```

### Step 2: Get a JWT Token

Scroll down to the **Auth Controller** section and find the `POST /auth/token` endpoint.

1. Click "Try it out"
2. Enter the request body:
```json
{
  "username": "admin",
  "password": "password"
}
```
3. Click "Execute"
4. Copy the token from the response (the long string after `"token": "`)

**Example Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTY4MDAwMDAwMCwiZXhwIjoxNjgwMDAzNjAwfQ.abc123...",
  "expiresIn": 3600000,
  "username": "admin"
}
```

### Step 3: Authorize in Swagger

1. Click the **"Authorize"** button at the top right of the Swagger page (green lock icon)
2. In the "bearerAuth" dialog, paste **only the token** (without "Bearer " prefix)
3. Click "Authorize"
4. Click "Close"

You should now see a closed lock icon 🔒 indicating you are authenticated.

### Step 4: Test Protected Endpoints

Try any protected endpoint, such as:
- `POST /api/weather/create` - Create weather data
- `PUT /api/weather/update/{id}` - Update weather data
- `DELETE /api/weather/delete/{id}` - Delete weather data
- `GET /api/valkey/**` - Valkey operations

The authorization header will be automatically included in all requests.

### Token Expiration

JWT tokens expire after **1 hour**. If you get 401 Unauthorized errors, repeat Steps 2-3 to get a new token.

---

## Using Postman

### Step 1: Get a JWT Token

1. Create a new POST request
2. Set URL to: `http://localhost:8080/auth/token`
3. Go to the **"Body"** tab
4. Select **"raw"** and **"JSON"**
5. Enter the request body:
```json
{
  "username": "admin",
  "password": "password"
}
```
6. Click **"Send"**
7. Copy the token from the response

### Step 2: Use Token for Protected Requests

For any protected endpoint:

1. Create your request (e.g., `POST http://localhost:8080/api/weather/create`)
2. Go to the **"Authorization"** tab
3. Select **Type: "Bearer Token"**
4. Paste the token in the **"Token"** field
5. Click **"Send"**

**Alternative: Manual Header**

You can also set the header manually:
- Go to the **"Headers"** tab
- Add a new header:
  - Key: `Authorization`
  - Value: `Bearer eyJhbGciOiJIUzI1NiJ9...` (include "Bearer " prefix)

### Step 3: Save Token as Environment Variable (Recommended)

For easier reuse:

1. After getting the token, click the response body
2. In the Tests tab of your token request, add:
```javascript
pm.test("Save token", function () {
    var jsonData = pm.response.json();
    pm.environment.set("jwt_token", jsonData.token);
});
```
3. In protected requests, use `{{jwt_token}}` in the Authorization Bearer Token field

---

## Example API Calls

### Create Weather Data (Protected)

**cURL:**
```bash
# First, get token
TOKEN=$(curl -s -X POST http://localhost:8080/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}' \
  | grep -o '"token":"[^"]*' | cut -d'"' -f4)

# Then use token
curl -X POST http://localhost:8080/api/weather/create \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "location": "San Francisco",
    "temperature": 18.5,
    "humidity": 65,
    "description": "Partly cloudy"
  }'
```

**Postman:**
1. Authorization: Bearer Token → paste your token
2. Body (JSON):
```json
{
  "location": "San Francisco",
  "temperature": 18.5,
  "humidity": 65,
  "description": "Partly cloudy"
}
```

### Read Weather Data (Public - No Auth Needed)

```bash
curl http://localhost:8080/api/weather/read
```

### Update Weather Data (Protected)

**Postman:**
1. PUT `http://localhost:8080/api/weather/update/1`
2. Authorization: Bearer Token
3. Body (JSON):
```json
{
  "location": "San Francisco",
  "temperature": 20.0,
  "humidity": 60,
  "description": "Sunny"
}
```

---

## Troubleshooting

### 401 Unauthorized Error

**Cause:** Missing, expired, or invalid token

**Solutions:**
- Get a new token from `/auth/token`
- Check that you copied the full token string
- In Swagger: paste token WITHOUT "Bearer " prefix
- In Postman: use Bearer Token type (adds prefix automatically)
- Verify token hasn't expired (1 hour expiration)

### 403 Forbidden Error

**Cause:** Valid token but insufficient permissions

**Solution:**
- Try logging in as "admin" user instead of "user"

### Token Not Working in Swagger

**Checklist:**
- Did you click "Authorize" button?
- Did you paste the token without "Bearer " prefix?
- Is the lock icon closed (🔒)?
- Try refreshing the page and re-authorizing

### Postman Says "Invalid Token"

**Checklist:**
- Is Bearer Token selected (not Basic Auth)?
- Did you copy the full token from the response?
- Check for extra spaces before/after the token
- Try the manual header approach: `Authorization: Bearer <token>`

---

## Security Notes

⚠️ **For Development Only**

The current JWT configuration is for development/testing only:

- Hardcoded secret key (insecure - change in production)
- Test users with simple passwords
- Long token expiration (1 hour)
- CORS open to all origins (`*`)

**For Production:**
- Use environment variables for secrets
- Implement proper user management (database)
- Use shorter token expiration with refresh tokens
- Configure CORS to specific allowed origins
- Enable HTTPS/TLS
- Implement rate limiting per user
- Add token revocation/blacklisting

---

## Additional Resources

- Gateway documentation: [GATEWAY_GUIDE.md](gateway/GATEWAY_GUIDE.md)
- Quick start guide: [QUICKSTART.md](QUICKSTART.md)
- OpenAPI spec: http://localhost:8080/v3/api-docs
- Health check: http://localhost:8080/actuator/health

## Support

For issues or questions about JWT authentication:
1. Verify both services are running: `lsof -i :8080` and `lsof -i :8081`
2. Check gateway logs: `tail -f gateway/logs/gateway.log`
3. Check backend logs: `tail -f logs/backend.log`
4. Test token generation: `curl -X POST http://localhost:8080/auth/token -H "Content-Type: application/json" -d '{"username":"admin","password":"password"}'`
