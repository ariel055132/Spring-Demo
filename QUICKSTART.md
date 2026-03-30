# Spring Demo with API Gateway - Quick Start Guide

## Project Overview

This repository contains:
1. **Spring-Demo Backend** (`demo/`) - REST API with Weather and Valkey services
2. **API Gateway** (`gateway/`) - Spring Cloud Gateway for routing, authentication, and rate limiting

## Architecture

```
┌─────────┐
│ Client  │
└────┬────┘
     │ HTTP
     ▼
┌──────────────────────────────────┐
│  API Gateway                     │
│  - JWT Authentication            │
│  - Rate Limiting (Valkey)        │
│  - Request/Response Logging      │
│  - API Versioning                │
│  - CORS                           │
└──────────────┬───────────────────┘
               │
               ▼
┌──────────────────────────────────┐
│  Spring-Demo                     │
│  - Weather API                   │
│  - Valkey Cache API              │
│  - PostgreSQL Database           │
└──────────────────────────────────┘
```

## Prerequisites

- **Java 21** - Required for Spring Boot 3.5.x
- **Maven 3.6+** - Build tool (wrapper included)
- **PostgreSQL** - Database 
- **Valkey/Redis** - Cache and rate limiting 

## Quick Start (All Services)

### 1. Start Infrastructure

```bash
# Start PostgreSQL (if not running)
# macOS with Homebrew:
brew services start postgresql

# Start Valkey/Redis
./start-tunnels.sh
```

### 2. Start Spring-Demo Backend

```bash
cd demo
./mvnw spring-boot:run

# Backend starts on http://localhost:8081
```

### 3. Start API Gateway

```bash
# Open a new terminal
cd gateway
./mvnw spring-boot:run

# Gateway starts on http://localhost:8080
```

### 4. Test the System

**Get JWT Token:**
```bash
curl -X POST http://localhost:8080/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username": "user", "password": "userpassword"}'
```

**Use Token to Access Protected API:**
```bash
export TOKEN="<your-token-here>"

curl -X POST http://localhost:8080/api/weather/create \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "header": {"requestId": "req-001", "timestamp": "2026-03-30T10:00:00"},
    "city": "Boston",
    "tempLo": 45,
    "tempHi": 60,
    "prcp": 0.0,
    "date": "2025-03-30"
  }'
```

**Test Public Endpoint (No Auth Required):**
```bash
curl -X POST http://localhost:8080/api/weather/read \
  -H "Content-Type: application/json" \
  -d '{
    "header": {"requestId": "req-002", "timestamp": "2026-03-30T10:00:00"},
    "city": "Boston",
    "date": "2025-03-30"
  }'
```

## API Endpoints

### Gateway Endpoints

| Endpoint | Method | Auth Required | Description |
|----------|--------|---------------|-------------|
| `/auth/token` | POST | No | Generate JWT token |
| `/auth/validate` | GET | Yes | Validate JWT token |
| `/actuator/health` | GET | No | Gateway health check |
| `/actuator/metrics` | GET | No | Gateway metrics |

### Backend Endpoints (via Gateway)

**Weather API:**
- `POST /api/weather/create` - Create weather record (Auth Required)
- `POST /api/weather/read` - Read weather record (Public)
- `PUT /api/weather/update` - Update weather record (Auth Required)
- `DELETE /api/weather/delete` - Delete weather record (Auth Required)

**Valkey API:**
- `POST /api/valkey/set` - Set key-value (Auth Required)
- `GET /api/valkey/get/{key}` - Get value (Auth Required)
- `DELETE /api/valkey/delete/{key}` - Delete key (Auth Required)
- And more... (see `/swagger-ui.html`)

## API Gateway Features

### 1. JWT Authentication
- **Test Users:** admin/user/test (password: "password")
- **Token Expiration:** 1 hour
- **Public Routes:** Health, Swagger, `/api/weather/read`, `/auth/token`

### 2. Rate Limiting
- **Weather API:** 100 requests/minute per client
- **Valkey API:** 50 requests/minute per client
- **Strategy:** IP-based (configurable to JWT-based)

### 3. API Versioning
- **Path-based:** `/api/v1/weather/read` → routes to backend
- **Header-based:** `X-API-Version: v1`
- **Default:** v1 if not specified

### 4. Request Tracking
- **X-Correlation-Id** - Automatically added to all requests
- **X-Response-Time** - Response processing duration
- **X-Gateway-Version** - Gateway version (1.0.0)

### 5. CORS
- **Allowed Origins:** `*` (development) - configure for production
- **Allowed Methods:** GET, POST, PUT, DELETE, OPTIONS
- **Exposed Headers:** X-Correlation-Id, X-Response-Time, X-Gateway-Version

## Documentation

### Gateway Documentation
- [Gateway Guide](gateway/GATEWAY_GUIDE.md) - Complete gateway documentation
- [Gateway README](gateway/README.md) - Quick reference

### Backend Documentation
- [HELP.md](demo/HELP.md) - Spring-Demo backend guide
- [VALKEY_DEMO.md](demo/VALKEY_DEMO.md) - Valkey integration guide
- [PRECHECK_GUIDE.md](demo/PRECHECK_GUIDE.md) - Validation framework guide

### Swagger UI
- **Gateway + Backend:** http://localhost:8080/swagger-ui.html
- **Backend Direct:** http://localhost:8081/swagger-ui.html

## Configuration

### Gateway Configuration
Edit `gateway/src/main/resources/application.yml`:
- **Port:** 8080
- **Backend URI:** http://localhost:8081
- **JWT Secret:** Change in production!
- **Rate Limits:** Adjust per endpoint

### Backend Configuration
Edit `demo/src/main/resources/application.properties`:
- **Port:** 8081 (changed from default 8080)
- **Database:** PostgreSQL connection settings
- **Valkey:** Redis connection settings

## Troubleshooting

### Gateway Won't Start
```bash
# Check if port 8080 is in use
lsof -i :8080
```

### Backend Won't Start
```bash
# Check if port 8081 is in use
lsof -i :8081
```

### Authentication Issues
- Verify token hasn't expired (1 hour lifetime)
- Check Authorization header format: `Bearer <token>`
- Review gateway logs: `gateway/logs/gateway.log`

### Rate Limiting Not Working
- Verify Valkey is running on port 6378
- Check gateway logs for rate limiter errors
- Test with `curl` in a loop to trigger rate limit

## Development

### Build Both Projects
```bash
# Build backend
cd demo && ./mvnw clean install

# Build gateway
cd gateway && ./mvnw clean install
```

### Run Tests
```bash
# Backend tests
cd demo && ./mvnw test

# Gateway tests (when added)
cd gateway && ./mvnw test
```

### Check for Errors
```bash
# Backend logs
tail -f demo/logs/application.log

# Gateway logs
tail -f gateway/logs/gateway.log
```

## Production Considerations

### Security
1. **Change JWT Secret** in `gateway/application.yml`
2. **Restrict CORS Origins** to specific domains
3. **Enable HTTPS/TLS** for all services
4. **Use OAuth2** instead of simple JWT endpoint

### Scalability
1. **Run Multiple Gateway Instances** behind load balancer
2. **Run Multiple Backend Instances** with service discovery
3. **Use External Valkey Cluster** for distributed caching
4. **Database Connection Pooling** tuning

### Monitoring
1. **Prometheus + Grafana** for metrics visualization
2. **Spring Cloud Sleuth + Zipkin** for distributed tracing
3. **ELK Stack** for centralized logging
4. **Alerting** for high error rates and downtime

## License

See project license files.

## Support

For issues:
- Check logs in `demo/logs/` and `gateway/logs/`
- Review documentation in respective README files
- Consult Spring Cloud Gateway docs: https://spring.io/projects/spring-cloud-gateway
