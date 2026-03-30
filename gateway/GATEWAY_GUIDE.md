# API Gateway Guide

## Overview

The API Gateway is a Spring Cloud Gateway service that provides a unified entry point for all microservices in the Spring-Demo architecture. It handles cross-cutting concerns such as authentication, rate limiting, logging, API versioning, and CORS.

## Architecture

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       │ HTTP Request
       ▼
┌──────────────────────────────────────────┐
│          API Gateway (Port 8080)         │
│                                          │
│  ┌────────────────────────────────────┐ │
│  │  Global Filters (Order)            │ │
│  │  -------------------------         │ │
│  │  1. CorrelationIdFilter (-200)    │ │
│  │  2. RequestLoggingFilter (-190)   │ │
│  │  3. ApiVersioningFilter (-150)    │ │
│  │  4. JwtAuthenticationFilter (-100)│ │
│  │  5. Rate Limiting (configured)    │ │
│  │  6. ResponseHeaderFilter (lowest) │ │
│  └────────────────────────────────────┘ │
│                                          │
│  ┌────────────────────────────────────┐ │
│  │  Features                          │ │
│  │  - JWT Authentication              │ │
│  │  - Rate Limiting (Valkey-backed)  │ │
│  │  - Request/Response Transformation│ │
│  │  - Logging & Monitoring           │ │
│  │  - API Versioning                 │ │
│  │  - CORS Handling                  │ │
│  └────────────────────────────────────┘ │
└──────────────┬───────────────────────────┘
               │
               │ Routed Request
               ▼
┌─────────────────────────────────────────┐
│    Spring-Demo Service (Port 8081)      │
│                                         │
│  - Weather API (/api/weather/**)       │
│  - Valkey API (/api/valkey/**)         │
│  - Health endpoint                      │
│  - Swagger UI                           │
└─────────────────────────────────────────┘
```

## Features

### 1. Request Routing

The gateway routes requests to backend services based on path patterns:

| Path Pattern | Backend Service | Rate Limit |
|--------------|----------------|------------|
| `/api/weather/**` | http://localhost:8081 | 100 req/min |
| `/api/valkey/**` | http://localhost:8081 | 50 req/min |
| `/actuator/health` | http://localhost:8081 | No limit |
| `/swagger-ui/**` | http://localhost:8081 | No limit |

### 2. JWT Authentication

All endpoints (except public routes) require JWT authentication.

**Public Routes** (no authentication required):
- `/actuator/health` - Health check
- `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**` - API documentation
- `/api/weather/read` - Read weather data
- `/auth/token` - Generate JWT token

**Protected Routes** (JWT required):
- All other endpoints require `Authorization: Bearer <token>` header

#### Getting a JWT Token

**Request:**
```bash
curl -X POST http://localhost:8080/auth/token \
  -H "Content-Type: application/json" \
  -d '{
    "username": "<username>",
    "password": "<password>"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 3600000,
  "username": "admin"
}
```

**Available Test Users:**

See `AuthController.java` for configured test users.

**Note:** Test credentials are for development only. Replace with proper user authentication in production.

#### Using JWT Token

**Request with JWT:**
```bash
curl -X POST http://localhost:8080/api/weather/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{
    "header": {
      "requestId": "req-123",
      "timestamp": "2026-03-30T10:00:00"
    },
    "city": "New York",
    "tempLo": 45,
    "tempHi": 60,
    "prcp": 0.0,
    "date": "2025-03-30"
  }'
```

**JWT Claims Structure:**
```json
{
  "sub": "admin",           // username
  "role": "USER",           // user role
  "iat": 1711800000,        // issued at (Unix timestamp)
  "exp": 1711803600         // expiration (Unix timestamp)
}
```

### 3. Rate Limiting

Rate limiting is enforced using Valkey (Redis-compatible) as a distributed store.

**Rate Limits:**
- **Weather API**: 100 requests/minute per client (burst: 150)
- **Valkey API**: 50 requests/minute per client (burst: 75)
- **Auth endpoint**: Implicitly limited by connection pool

**Rate Limit Strategy:**
- Key: Client IP address
- Alternative: JWT subject (uncomment `jwtKeyResolver` in `ValkeyConfig.java`)

**Response when rate limit exceeded:**
```
HTTP/1.1 429 Too Many Requests
X-RateLimit-Remaining: 0
X-RateLimit-Replenish-Rate: 100
X-RateLimit-Burst-Capacity: 150
```

### 4. Request/Response Transformation

#### Request Headers Added by Gateway:

| Header | Description | Example |
|--------|-------------|---------|
| `X-Correlation-Id` | Unique request ID for tracing | `550e8400-e29b-41d4-a716-446655440000` |
| `X-Auth-User` | Authenticated username (if JWT present) | `admin` |
| `X-API-Version` | API version used | `v1` |

#### Response Headers Added by Gateway:

| Header | Description | Example |
|--------|-------------|---------|
| `X-Correlation-Id` | Same as request correlation ID | `550e8400-e29b-41d4-a716-446655440000` |
| `X-Response-Time` | Request processing duration | `45ms` |
| `X-Gateway-Version` | Gateway version | `1.0.0` |

### 5. API Versioning

Two versioning strategies are supported:

#### Path-based Versioning (Recommended)
```bash
# Access v1 API via path
curl http://localhost:8080/api/v1/weather/read \
  -H "Content-Type: application/json" \
  -d '{"city": "Boston", "date": "2025-03-30"}'

# Path is rewritten to: /api/weather/read
# Header X-API-Version: v1 is added
```

#### Header-based Versioning
```bash
# Access v1 API via header
curl http://localhost:8080/api/weather/read \
  -H "Content-Type: application/json" \
  -H "X-API-Version: v1" \
  -d '{"city": "Boston", "date": "2025-03-30"}'
```

**Default Version:** If no version is specified, `v1` is used by default (configurable in `application.yml`).

### 6. Logging & Monitoring

#### Request/Response Logging

All requests and responses are logged with:
- HTTP method and path
- Client IP address
- Correlation ID
- Response status code
- Processing time

**Log Format:**
```
2026-03-30 10:15:30.123 [reactor-http-nio-2] INFO  c.e.g.filter.RequestLoggingFilter - Incoming request: method=POST, path=/api/weather/create, client=127.0.0.1, correlationId=550e8400-e29b-41d4-a716-446655440000, userAgent=curl/7.68.0

2026-03-30 10:15:30.178 [reactor-http-nio-2] INFO  c.e.g.filter.ResponseHeaderFilter - Response: path=/api/weather/create, status=200, correlationId=550e8400-e29b-41d4-a716-446655440000
```

**Log Files:**
- **Location:** `gateway/logs/gateway.log`
- **Rotation:** Daily (30-day retention)
- **Format:** Timestamp, thread, level, logger, message

#### Actuator Endpoints

Spring Boot Actuator provides monitoring endpoints:

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Application health status |
| `/actuator/metrics` | Application metrics |
| `/actuator/prometheus` | Prometheus-compatible metrics |
| `/actuator/info` | Application information |

**Example: Check Gateway Health**
```bash
curl http://localhost:8080/actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "diskSpace": {"status": "UP"},
    "ping": {"status": "UP"},
    "redis": {"status": "UP"}
  }
}
```

**Example: View Metrics**
```bash
# List all available metrics
curl http://localhost:8080/actuator/metrics

# View specific metric (e.g., JVM memory)
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

### 7. CORS Configuration

Cross-Origin Resource Sharing (CORS) is enabled globally:

**Configuration:**
- **Allowed Origins:** `*` (all origins - change for production)
- **Allowed Methods:** GET, POST, PUT, DELETE, OPTIONS
- **Allowed Headers:** All headers
- **Exposed Headers:** `X-Correlation-Id`, `X-Response-Time`, `X-Gateway-Version`
- **Max Age:** 3600 seconds (1 hour)

**Preflight Request Example:**
```bash
curl -X OPTIONS http://localhost:8080/api/weather/read \
  -H "Origin: http://example.com" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Authorization, Content-Type"
```

**Response includes CORS headers:**
```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
Access-Control-Allow-Headers: Authorization, Content-Type
Access-Control-Max-Age: 3600
```

## Getting Started

### Prerequisites

1. **Java 21** - Required for Spring Boot 3.5.x
2. **Maven 3.6+** - Build tool
3. **Valkey/Redis** - Running and configured (see CONFIG.md for secure configuration)
4. **Spring-Demo Service** - Backend service running on port 8081

### Installation

1. **Navigate to gateway directory:**
```bash
cd gateway
```

2. **Build the project:**
```bash
./mvnw clean install
```

3. **Start the gateway:**
```bash
./mvnw spring-boot:run
```

**Gateway starts on port 8080**

### Configuration

Gateway configuration is in `src/main/resources/application.yml`:

```yaml
# Key configuration sections:
server:
  port: 8080  # Gateway port

spring:
  cloud:
    gateway:
      routes:  # Define backend routes
      globalcors:  # CORS configuration

jwt:
  secret: ${JWT_SECRET:...}  # Set via environment variable
  expiration: 3600000  # Token expiration (1 hour)

api:
  version:
    default: v1  # Default API version
```

**⚠️ Security Warning:** 
- All sensitive configuration should use environment variables or secrets management
- See `CONFIG.md` for detailed secure configuration instructions
- Never commit passwords or secrets to version control

## Testing

### 1. Start Services

```bash
# Terminal 1: Start Valkey (if not running)
cd <project-root>
./start-tunnels.sh

# Terminal 2: Start Spring-Demo backend
cd <project-root>/demo
./mvnw spring-boot:run
# Backend runs on port 8081

# Terminal 3: Start API Gateway
cd <project-root>/gateway
./mvnw spring-boot:run
# Gateway runs on port 8080
```

### 2. Test Public Endpoint (No Auth)

```bash
curl -X POST http://localhost:8080/api/weather/read \
  -H "Content-Type: application/json" \
  -d '{
    "header": {
      "requestId": "req-001",
      "timestamp": "2026-03-30T10:00:00"
    },
    "city": "Boston",
    "date": "2025-03-30"
  }'
```

### 3. Test JWT Authentication

**Step 1: Get JWT token**
```bash
curl -X POST http://localhost:8080/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username": "<username>", "password": "<password>"}'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInN1YiI6ImFkbWluIiwiaWF0IjoxNzExODAwMDAwLCJleHAiOjE3MTE4MDM2MDB9...",
  "expiresIn": 3600000,
  "username": "admin"
}
```

**Step 2: Use token to create weather data (protected endpoint)**
```bash
export TOKEN="<paste-token-here>"

curl -X POST http://localhost:8080/api/weather/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "header": {
      "requestId": "req-002",
      "timestamp": "2026-03-30T10:00:00"
    },
    "city": "New York",
    "tempLo": 45,
    "tempHi": 60,
    "prcp": 0.0,
    "date": "2025-03-30"
  }'
```

**Step 3: Test without token (expect 401)**
```bash
curl -X POST http://localhost:8080/api/weather/create \
  -H "Content-Type: application/json" \
  -d '{...}'

# Response: HTTP 401 Unauthorized
```

### 4. Test Rate Limiting

**Send 101 requests rapidly (expect 429 on last request):**
```bash
for i in {1..101}; do
  echo "Request $i:"
  curl -s -o /dev/null -w "%{http_code}\n" \
    -X POST http://localhost:8080/api/weather/read \
    -H "Content-Type: application/json" \
    -d '{
      "header": {"requestId": "req-'$i'", "timestamp": "2026-03-30T10:00:00"},
      "city": "Boston",
      "date": "2025-03-30"
    }'
done
```

**Expected:**
- Requests 1-100: `200 OK`
- Request 101: `429 Too Many Requests`

### 5. Test API Versioning

**Path-based:**
```bash
curl -X POST http://localhost:8080/api/v1/weather/read \
  -H "Content-Type: application/json" \
  -d '{
    "header": {"requestId": "req-v1", "timestamp": "2026-03-30T10:00:00"},
    "city": "Boston",
    "date": "2025-03-30"
  }'
```

**Header-based:**
```bash
curl -X POST http://localhost:8080/api/weather/read \
  -H "Content-Type: application/json" \
  -H "X-API-Version: v1" \
  -d '{
    "header": {"requestId": "req-v1", "timestamp": "2026-03-30T10:00:00"},
    "city": "Boston",
    "date": "2025-03-30"
  }'
```

### 6. Test CORS

```bash
curl -X OPTIONS http://localhost:8080/api/weather/read \
  -H "Origin: http://example.com" \
  -H "Access-Control-Request-Method: POST" \
  -i
```

**Check for CORS headers in response:**
- `Access-Control-Allow-Origin: *`
- `Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS`

### 7. Test Monitoring

**Health check:**
```bash
curl http://localhost:8080/actuator/health
```

**Metrics:**
```bash
curl http://localhost:8080/actuator/metrics
```

**Check logs:**
```bash
tail -f gateway/logs/gateway.log
```

## Troubleshooting

### Gateway Won't Start

**Error: Port 8080 already in use**
```
Solution: 
1. Check if another process is using port 8080:
   lsof -i :8080
2. Kill the process or change gateway port in application.yml
```

**Error: Cannot connect to Valkey**
```
Solution:
1. Verify Valkey is running:
   redis-cli -h localhost -p 6378 ping
2. Check credentials in application.yml match Valkey ACL config
3. Review Valkey logs for authentication errors
```

### Backend Service Not Responding

**Error: 503 Service Unavailable**
```
Solution:
1. Verify Spring-Demo backend is running on port 8081:
   curl http://localhost:8081/actuator/health
2. Check gateway routes in application.yml point to correct URI
3. Review gateway logs for connection errors
```

### JWT Authentication Issues

**Error: 401 Unauthorized even with valid token**
```
Solution:
1. Verify token is not expired (1 hour expiration)
2. Check Authorization header format: "Bearer <token>"
3. Ensure jwt.secret in application.yml matches token signing key
4. Review gateway logs for JWT validation errors
```

### Rate Limiting Not Working

**Error: No rate limiting applied**
```
Solution:
1. Verify Valkey connection (gateway uses Valkey for rate limit state)
2. Check RedisRateLimiter configuration in application.yml
3. Review ValkeyConfig.java key resolver implementation
4. Check gateway logs for rate limiter errors
```

## Production Considerations

### Security

1. **JWT Secret:**
   - Use a strong 256-bit secret key
   - Store in environment variable or secrets manager
   - Rotate periodically

2. **CORS:**
   - Restrict `allowedOrigins` to specific domains
   - Remove `*` wildcard in production

3. **Rate Limits:**
   - Adjust limits based on expected traffic
   - Consider per-user limits (JWT-based key resolver)

4. **HTTPS:**
   - Enable SSL/TLS in production
   - Use Let's Encrypt or corporate certificates

### Performance

1. **Connection Pooling:**
   - Tune Valkey connection pool size
   - Monitor Valkey connection metrics

2. **Gateway Instances:**
   - Run multiple gateway instances behind load balancer
   - Use sticky sessions if needed

3. **Caching:**
   - Consider adding response caching for read-heavy endpoints
   - Use Valkey for distributed cache

### Monitoring

1. **Metrics Collection:**
   - Integrate with Prometheus + Grafana
   - Monitor rate limit hits, error rates, latency

2. **Distributed Tracing:**
   - Add Spring Cloud Sleuth + Zipkin
   - Use correlation IDs for request tracing

3. **Alerting:**
   - Set up alerts for high error rates
   - Monitor gateway health and availability

## Future Enhancements

### Service Discovery
- Add Eureka or Consul for dynamic service registration
- Enable load balancing across multiple backend instances

### Circuit Breaker
- Add Resilience4j for fault tolerance
- Implement fallback responses when backends fail

### OAuth2 Integration
- Replace simple JWT endpoint with Spring Authorization Server
- Support OAuth2 authorization code flow

### API Documentation
- Aggregate OpenAPI specs from all services
- Display unified Swagger UI at gateway level

## Support

For issues or questions:
- Check gateway logs: `gateway/logs/gateway.log`
- Review Spring-Demo backend logs: `demo/logs/application.log`
- Consult Spring Cloud Gateway documentation: https://spring.io/projects/spring-cloud-gateway
