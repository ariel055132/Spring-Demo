# Valkey Demo - Usage Guide

## What's Been Created

This demo includes:
1. **ValkeyConfig** - Configuration class for Valkey/Redis connection
2. **ValkeyService** - Service layer with common Valkey operations
3. **ValkeyController** - REST API endpoints to test Valkey functionality

## Prerequisites

- Valkey server running at: `167.71.219.2:6378` (configured in application.properties)

## Running the Application

```bash
./mvnw spring-boot:run
```

## Available API Endpoints

### 1. Test Connection
```bash
curl http://localhost:8080/api/valkey/test
```

### 2. Set a Key-Value Pair
```bash
# Without expiry
curl -X POST "http://localhost:8080/api/valkey/set?key=name&value=John"

# With expiry (30 seconds)
curl -X POST "http://localhost:8080/api/valkey/set?key=temp&value=data&expirySeconds=30"
```

### 3. Get a Value by Key
```bash
curl "http://localhost:8080/api/valkey/get?key=name"
```

### 4. Check if Key Exists
```bash
curl "http://localhost:8080/api/valkey/exists?key=name"
```

### 5. Delete a Key
```bash
curl -X DELETE "http://localhost:8080/api/valkey/delete?key=name"
```

### 6. Increment Counter
```bash
curl -X POST "http://localhost:8080/api/valkey/increment?key=counter"
```

### 7. Decrement Counter
```bash
curl -X POST "http://localhost:8080/api/valkey/decrement?key=counter"
```

### 8. Get Time-To-Live (TTL)
```bash
curl "http://localhost:8080/api/valkey/ttl?key=temp"
```

## Example Usage Flow

```bash
# 1. Test connection
curl http://localhost:8080/api/valkey/test

# 2. Set a value
curl -X POST "http://localhost:8080/api/valkey/set?key=user:1&value=Alice"

# 3. Get the value
curl "http://localhost:8080/api/valkey/get?key=user:1"

# 4. Set a counter
curl -X POST "http://localhost:8080/api/valkey/set?key=visits&value=0"

# 5. Increment the counter
curl -X POST "http://localhost:8080/api/valkey/increment?key=visits"
curl -X POST "http://localhost:8080/api/valkey/increment?key=visits"

# 6. Get the counter value
curl "http://localhost:8080/api/valkey/get?key=visits"
```

## Configuration

Valkey connection settings in `application.properties`:
```properties
spring.data.redis.host=[host]
spring.data.redis.port=[port]
```

## Dependencies

- `spring-boot-starter-data-redis` - Spring Data Redis support
- `valkey-java:5.5.0` - Valkey Java client (Redis-compatible)
