# API Gateway

Spring Cloud Gateway for microservices routing, authentication, and rate limiting.

## Features

- ✅ **Request Routing** - Route requests to backend services
- ✅ **JWT Authentication** - Secure endpoints with JWT tokens
- ✅ **Rate Limiting** - Valkey-backed distributed rate limiting
- ✅ **Request/Response Transformation** - Add correlation IDs, headers, logging
- ✅ **API Versioning** - Path-based and header-based versioning
- ✅ **CORS Handling** - Global CORS configuration
- ✅ **Monitoring** - Actuator endpoints for health and metrics

## Quick Start

```bash
# Build
./mvnw clean install

# Run
./mvnw spring-boot:run

# Gateway starts on http://localhost:8080
```

## Prerequisites

- Java 21
- Maven 3.6+
- Valkey/Redis on localhost:6378
- Spring-Demo backend on localhost:8081

## Documentation

See [GATEWAY_GUIDE.md](GATEWAY_GUIDE.md) for complete documentation including:
- Architecture overview
- Feature details
- Configuration guide
- Testing instructions
- Troubleshooting tips

## Example Usage

### Get JWT Token
```bash
curl -X POST http://localhost:8080/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password"}'
```

### Use Token to Access Protected Endpoint
```bash
curl -X POST http://localhost:8080/api/weather/create \
  -H "Authorization: Bearer <your-token>" \
  -H "Content-Type: application/json" \
  -d '{"header": {...}, "city": "Boston", ...}'
```

## Configuration

Key configuration in `src/main/resources/application.yml`:

- Port: `8080`
- Backend: `http://localhost:8081`
- Valkey: `localhost:6378`
- JWT expiration: `1 hour`
- Rate limits: Weather (100/min), Valkey (50/min)

## Tech Stack

- Spring Boot 3.5.10
- Spring Cloud Gateway 2024.0.0
- Spring Security + JWT (jjwt 0.12.5)
- Spring Data Redis Reactive (Valkey)
- Java 21

## License

See parent project license.
