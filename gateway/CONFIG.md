# Configuration Guide

## Environment-Based Configuration

This gateway uses Spring Boot profiles and environment variables for secure configuration management.

## Quick Start

### Option 1: Using application-local.yml (Recommended for Development)

1. Copy the example file:
```bash
cd gateway/src/main/resources
cp application-local.yml.example application-local.yml
```

2. Edit `application-local.yml` with your actual credentials

3. Run with local profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### Option 2: Using Environment Variables (Recommended for Production)

Set environment variables before starting the application:

```bash
export REDIS_HOST=localhost
export REDIS_PORT=6378
export REDIS_USERNAME=default
export REDIS_PASSWORD=your_password
export REDIS_DATABASE=0
export JWT_SECRET=your_jwt_secret_min_256_bits
export JWT_EXPIRATION=3600000
```

Then start the gateway:
```bash
./mvnw spring-boot:run
```

### Option 3: System Properties

```bash
./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="\
  --spring.data.redis.password=your_password \
  --jwt.secret=your_secret"
```

## Configuration Properties

### Redis/Valkey Configuration

| Property | Environment Variable | Default | Description |
|----------|---------------------|---------|-------------|
| `spring.data.redis.host` | `REDIS_HOST` | `localhost` | Redis/Valkey host |
| `spring.data.redis.port` | `REDIS_PORT` | `6378` | Redis/Valkey port |
| `spring.data.redis.username` | `REDIS_USERNAME` | `default` | Redis/Valkey username |
| `spring.data.redis.password` | `REDIS_PASSWORD` | `changeme` | Redis/Valkey password |
| `spring.data.redis.database` | `REDIS_DATABASE` | `0` | Redis/Valkey database number |
| `spring.data.redis.ssl.enabled` | `REDIS_SSL_ENABLED` | `false` | Enable SSL/TLS |

### JWT Configuration

| Property | Environment Variable | Default | Description |
|----------|---------------------|---------|-------------|
| `jwt.secret` | `JWT_SECRET` | *(default key)* | JWT signing secret (min 256 bits) |
| `jwt.expiration` | `JWT_EXPIRATION` | `3600000` | Token expiration (milliseconds) |

## Security Best Practices

### Development vs Production

**Development:**
- Use `application-local.yml` (gitignored)
- Simple passwords acceptable
- Local services (localhost)

**Production:**
- Use environment variables or secrets management (AWS Secrets Manager, HashiCorp Vault, etc.)
- Strong passwords (min 32 chars, random)
- Separate Redis instances per environment
- Enable Redis SSL/TLS
- Use managed Redis services (AWS ElastiCache, Redis Enterprise, etc.)

### What NOT to Do

❌ **Never commit:**
- `application-local.yml` (already gitignored)
- Passwords or secrets in `application.yml`
- Environment files (`.env`)

❌ **Never hardcode:**
- Passwords in Java source code
- API keys or secrets in configuration classes
- Connection strings with credentials

✅ **Always:**
- Use environment variables or external configuration
- Keep secrets out of version control
- Use different credentials per environment
- Rotate secrets regularly
- Use strong, random passwords

## Files

### Tracked in Git (Safe to Commit)
- `application.yml` - Default configuration with placeholders
- `application-local.yml.example` - Template for local development

### NOT Tracked (Gitignored)
- `application-local.yml` - Your actual local credentials
- `.env` - Environment variable files

## Docker/Kubernetes Configuration

### Docker Compose

```yaml
services:
  gateway:
    image: api-gateway:latest
    environment:
      - REDIS_HOST=valkey
      - REDIS_PORT=6379
      - REDIS_PASSWORD=${VALKEY_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
    env_file:
      - .env.production
```

### Kubernetes Secret

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: gateway-secrets
type: Opaque
stringData:
  redis-password: your_redis_password
  jwt-secret: your_jwt_secret
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-gateway
spec:
  template:
    spec:
      containers:
      - name: gateway
        env:
        - name: REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: gateway-secrets
              key: redis-password
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: gateway-secrets
              key: jwt-secret
```

## Troubleshooting

### "Could not connect to Redis"

**Check:**
1. Redis/Valkey is running: `lsof -i :6378`
2. Correct host/port in configuration
3. Correct password
4. Network connectivity

**Test connection:**
```bash
# Using redis-cli
redis-cli -h localhost -p 6378 -a your_password ping

# Using valkey-cli
valkey-cli -h localhost -p 6378 -a your_password ping
```

### "JWT signature does not match"

**Cause:** JWT secret changed between token generation and validation

**Solution:**
- Use consistent JWT secret across all gateway instances
- Store JWT secret in shared configuration (environment variable)

### Environment variables not loaded

**Check:**
1. Variables are exported: `echo $REDIS_PASSWORD`
2. Running in correct shell session
3. Spring profile is active: `-Dspring.profiles.active=local`

## Migration from Hardcoded Configuration

If upgrading from hardcoded configuration:

1. **Create** `application-local.yml` with your actual credentials
2. **Remove** manual Redis configuration beans (if any)
3. **Update** any hardcoded values to use properties
4. **Test** connection with new configuration
5. **Verify** application starts and connects successfully

## Additional Resources

- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Spring Cloud Config](https://spring.io/projects/spring-cloud-config)
- [12-Factor App Config](https://12factor.net/config)
