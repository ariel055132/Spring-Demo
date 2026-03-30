# Spring Demo Multi-Module Project

A multi-module Maven project demonstrating microservices architecture with shared foundation library, backend service, and API gateway.

## 🏗️ Project Structure

```
Spring-Demo/
├── pom.xml                    # Parent POM (manages all modules)
├── foundation/                # Shared library module
│   ├── pom.xml
│   └── src/main/java/com/example/foundation/
│       ├── api/              # Base request/response models
│       ├── checker/          # PreCheck validation framework
│       ├── exception/        # Global exception handlers
│       └── util/             # Utility classes
├── demo/                      # Backend microservice
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/example/demo/
│       │   ├── controller/   # REST controllers
│       │   ├── service/      # Business logic
│       │   ├── repository/   # Data access
│       │   ├── entity/       # JPA entities
│       │   └── config/       # Configuration classes
│       └── resources/
│           └── application.properties
└── gateway/                   # API Gateway
    ├── pom.xml
    └── src/
        ├── main/java/com/example/gateway/
        │   ├── filter/       # Gateway filters (JWT, logging, etc.)
        │   ├── config/       # Configuration classes
        │   ├── controller/   # Auth controller
        │   ├── model/        # DTOs
        │   └── util/         # JWT utilities
        └── resources/
            └── application.yml
```

## 📦 Modules

### 1. Foundation (`foundation/`)

**Purpose:** Shared library containing reusable infrastructure components

**Package:** `com.example.foundation`

**Key Components:**
- **API Models** (`foundation.api`):
  - `BaseRequest` - Standard request wrapper with header
  - `BaseResponse` - Standard response wrapper with status
  - `RequestHeader` - Request metadata (ID, timestamp)

- **Validation Framework** (`foundation.checker`):
  - `@PreCheck` - Annotation for declarative validation
  - `PreCheckHandler` - Interface for custom validators
  - `PreCheckAspect` - AOP interceptor for pre-validation
  - `BaseChecker` - Abstract base class for validators

- **Exception Handling** (`foundation.exception`):
  - `GlobalExceptionHandler` - Centralized exception handling

- **Utilities** (`foundation.util`):
  - `LogUtil` - Logging helpers

**Dependencies:**
- Spring Boot Web
- Spring AOP
- Lombok

**Build Output:** `foundation-1.0.0-SNAPSHOT.jar` (library, not executable)

### 2. Demo Backend (`demo/`)

**Purpose:** Main backend service for Weather and Valkey operations

**Package:** `com.example.demo`

**Key Features:**
- Weather data CRUD operations with PostgreSQL
- Valkey (Redis-compatible) key-value operations
- PreCheck validation using foundation library
- RESTful API with OpenAPI/Swagger documentation

**Dependencies:**
- Foundation module (internal)
- Spring Boot Web, JPA, Data Redis
- PostgreSQL driver
- Valkey Java client
- SpringDoc OpenAPI

**Runs on:** Port 8081

**Build Output:** `demo-1.0.0-SNAPSHOT.jar` (executable Spring Boot app)

### 3. API Gateway (`gateway/`)

**Purpose:** API Gateway for routing, authentication, and cross-cutting concerns

**Package:** `com.example.gateway`

**Key Features:**
- JWT authentication
- Rate limiting (Valkey-backed)
- Request routing to backend services
- CORS handling
- Request/response logging and transformation
- API versioning

**Dependencies:**
- Spring Cloud Gateway
- Spring Security
- Spring Data Redis Reactive
- JWT (jjwt)

**Runs on:** Port 8080

**Build Output:** `gateway-1.0.0-SNAPSHOT.jar` (executable Spring Boot app)

## 🚀 Building the Project

### Build All Modules

From the root directory:

```bash
# Clean and build all modules
./mvnw clean install

# Skip tests for faster build
./mvnw clean install -DskipTests

# Build specific module
./mvnw clean install -pl foundation
./mvnw clean install -pl demo
./mvnw clean install -pl gateway
```

### Build Order

Maven Reactor automatically builds in correct order:
1. **foundation** - Built first (no dependencies)
2. **demo** - Built second (depends on foundation)
3. **gateway** - Built third (independent)

## 🏃 Running the Project

### Option 1: Run from Module Directories

```bash
# Terminal 1: Start backend (port 8081)
cd demo
./mvnw spring-boot:run

# Terminal 2: Start gateway (port 8080)
cd gateway
./mvnw spring-boot:run
```

### Option 2: Run JAR Files

```bash
# After mvn clean install
java -jar demo/target/demo-1.0.0-SNAPSHOT.jar
java -jar gateway/target/gateway-1.0.0-SNAPSHOT.jar
```

### Option 3: Use QUICKSTART.md Script

See [QUICKSTART.md](QUICKSTART.md) for automated startup scripts.

## 📚 Development Workflow

### Adding a New Module

1. Create module directory: `mkdir new-service`
2. Create `new-service/pom.xml` with parent reference
3. Add module to parent `pom.xml`: `<module>new-service</module>`
4. Add foundation dependency if needed:
   ```xml
   <dependency>
       <groupId>com.example</groupId>
       <artifactId>foundation</artifactId>
   </dependency>
   ```

### Using Foundation in New Services

```java
// Import foundation classes
import com.example.foundation.api.BaseRequest;
import com.example.foundation.api.BaseResponse;
import com.example.foundation.checker.PreCheck;
import com.example.foundation.checker.PreCheckHandler;

// Use PreCheck validation
@PreCheck(MyRequestChecker.class)
@PostMapping("/my-endpoint")
public BaseResponse<MyData> myEndpoint(@RequestBody BaseRequest request) {
    // Your business logic
}
```

### Updating Foundation Library

1. Make changes in `foundation/src/`
2. Build foundation: `./mvnw clean install -pl foundation`
3. Rebuild dependent modules: `./mvnw clean install -pl demo`
4. Test changes in demo/gateway

## 🔧 Maven Configuration

### Parent POM Features

- **Dependency Management:** Central version management for all dependencies
- **Plugin Management:** Shared plugin configurations
- **Properties:** Common properties (Java version, encoding, versions)
- **Build Configuration:** Lombok annotation processing, Spring Boot packaging

### Version Management

All module versions are synchronized:
- Parent: `1.0.0-SNAPSHOT`
- Foundation: `1.0.0-SNAPSHOT`
- Demo: `1.0.0-SNAPSHOT`
- Gateway: `1.0.0-SNAPSHOT`

To update all versions:
```bash
./mvnw versions:set -DnewVersion=2.0.0-SNAPSHOT
```

## 🎯 Benefits of Multi-Module Structure

### ✅ Advantages

1. **Code Reuse:** Foundation library shared across services
2. **Consistency:** Unified validation, error handling, and API patterns
3. **Single Build:** One command builds everything
4. **Version Synchronization:** All modules use same version
5. **Dependency Management:** Centralized in parent POM
6. **Local Development:** No need to publish to Maven repo
7. **Refactoring:** Easy to move code between modules
8. **CI/CD:** Single pipeline builds all modules

### 📊 When to Add New Modules

Add a new module when:
- Creating a new microservice with domain-specific logic
- Extracting reusable components (like foundation)
- Separating concerns (e.g., admin service vs user service)
- Different deployment requirements

Don't create a module for:
- Small utility classes (add to foundation)
- Single-use code specific to one service
- Premature abstraction

## 🧪 Testing

### Test All Modules

```bash
./mvnw test
```

### Test Specific Module

```bash
./mvnw test -pl foundation
./mvnw test -pl demo
./mvnw test -pl gateway
```

### Test with Coverage

```bash
./mvnw clean test jacoco:report
```

Coverage reports in:
- `foundation/target/site/jacoco/`
- `demo/target/site/jacoco/`
- `gateway/target/site/jacoco/`

## 📝 Documentation

- **[GATEWAY_GUIDE.md](gateway/GATEWAY_GUIDE.md)** - API Gateway features and usage
- **[JWT_AUTHORIZATION_GUIDE.md](JWT_AUTHORIZATION_GUIDE.md)** - JWT authentication guide
- **[CONFIG.md](gateway/CONFIG.md)** - Secure configuration management
- **[QUICKSTART.md](QUICKSTART.md)** - Quick start guide
- **[VALKEY_DEMO.md](VALKEY_DEMO.md)** - Valkey/Redis usage examples

## 🔍 Troubleshooting

### "Cannot resolve symbol" in IDE

**Solution:** Reimport Maven project
- IntelliJ: Right-click parent pom.xml → Maven → Reload Project
- Eclipse: Right-click project → Maven → Update Project
- VS Code: Cmd/Ctrl + Shift + P → Java: Clean Java Language Server Workspace

### "Module not found" error

**Solution:** Build foundation first
```bash
./mvnw clean install -pl foundation
```

### "Version conflict" warnings

**Solution:** Use dependency management in parent POM
```bash
./mvnw dependency:tree  # View dependency tree
```

### Changes in foundation not reflected

**Solution:** Reinstall foundation module
```bash
./mvnw clean install -pl foundation
./mvnw clean package -pl demo  # Rebuild dependent module
```

## 🚢 Deployment

### Building for Production

```bash
# Build with production profile
./mvnw clean package -Pprod

# Build Docker images (if Dockerfile exists)
docker build -t spring-demo/demo:latest ./demo
docker build -t spring-demo/gateway:latest ./gateway
```

### Docker Compose Example

```yaml
version: '3.8'
services:
  foundation:
    # Foundation is a library, not deployed separately
    
  demo:
    image: spring-demo/demo:latest
    ports:
      - "8081:8081"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - postgres
      - valkey
      
  gateway:
    image: spring-demo/gateway:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - demo
      - valkey
```

## 🤝 Contributing

When adding new features:
1. Consider if code belongs in foundation (reusable) or service (specific)
2. Update parent POM if adding new dependencies
3. Run `./mvnw clean install` to verify all modules build
4. Update relevant documentation

## 📄 License

Apache 2.0

## 🔗 Related Projects

- [Spring Cloud](https://spring.io/projects/spring-cloud)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Valkey](https://valkey.io/)
