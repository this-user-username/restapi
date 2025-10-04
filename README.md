# Device Management REST API

A Spring Boot REST API for managing device resources supporting CRUD operations, validation and containerized deployment.

## Features

- **CRUD Operations**: Create, read, update, delete devices
- **Filtering**: Filter devices by brand and state
- **Business Logic Validation**:
  - Restricted updates for in-use devices
  - Prevention of deleting in-use devices
  - Auditing properties
- **Database Management**: Liquibase migrations for schema versioning
- **Testing**: Unit tests, integration tests, and contract tests
- **API Documentation**: OpenAPI 3.0 (Swagger) documentation
- **Containerization**: Docker build support

## Technology Stack

- **Java 21**
- **Spring Boot 3.5.6**
- **Spring Data JPA**
- **Spring HATEOAS** (hypermedia-driven REST APIs)
- **PostgreSQL** (production)
- **H2** (testing)
- **Liquibase** (database migrations)
- **Docker & Docker Compose (for local testing)**
- **Gradle** (build tool)
- **OpenAPI 3.0** (API documentation)
- **Spring Cloud Contract** (contract testing)

## Device Domain

Each device has the following properties:

| Field              | Type          | Description            | Constraints                            |
|--------------------|---------------|------------------------|----------------------------------------|
| `id`               | Long          | Unique identifier      | Auto-generated                         |
| `name`             | String        | Device name            | Required, cannot be updated if in-use  |
| `brand`            | String        | Device brand           | Required, cannot be updated if in-use  |
| `state`            | Enum          | Device state           | Required (AVAILABLE, IN_USE, INACTIVE) |
| `creationTime`     | LocalDateTime | Creation timestamp     | Auto-generated, immutable              |
| `modificationTime` | LocalDateTime | Modification timestamp | Auto-generated                         |
| `version`          | Long          | Entity version         | Auto-generated                         |

## HATEOAS Implementation

This API implements **HATEOAS** (Hypermedia as the Engine of Application State) principles, providing hypermedia links in all responses. This makes the API self-descriptive and allows clients to discover available actions dynamically.

### Benefits
- **Self-Discovery**: Clients can navigate the API without prior knowledge of endpoints
- **Loose Coupling**: Reduces client dependency on hardcoded URLs
- **Evolvability**: API structure can change without breaking clients that follow links

### Response Structure
All device resources include hypermedia links under the `_links` section:
- `self`: Link to the current resource
- `devices`: Link to the devices collection
- Additional contextual links based on the current state

### Pagination Support
Collection endpoints return paginated results with HATEOAS links for navigation:
- `first`, `prev`, `next`, `last`: Navigation links
- `self`: Current page link

## API Endpoints

### Base URL: `/api/v1/devices`

| Method   | Endpoint              | Description                    |
|----------|----------------------|--------------------------------|
| `POST`   | `/`                  | Create a new device            |
| `GET`    | `/{id}`              | Get device by ID               |
| `GET`    | `/`                  | Get all devices (paginated)    |
| `GET`    | `/?brand={brand}`    | Get devices by brand           |
| `GET`    | `/?state={state}`    | Get devices by state           |
| `PUT`    | `/{id}`              | Fully update device            |
| `PATCH`  | `/{id}`              | Partially update device        |
| `DELETE` | `/{id}`              | Delete device                  |

### Example API Calls

#### Create a Device
```bash
curl -X POST http://localhost:8080/api/v1/devices \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15",
    "brand": "Apple",
    "state": "available"
  }'
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "iPhone 15",
  "brand": "Apple",
  "state": "available",
  "creationTime": "2023-10-01T12:00:00",
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/v1/devices/1"
    },
    "devices": {
      "href": "http://localhost:8080/api/v1/devices"
    }
  }
}
```

#### Get Device by ID
```bash
curl http://localhost:8080/api/v1/devices/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "iPhone 15",
  "brand": "Apple",
  "state": "available",
  "creationTime": "2023-10-01T12:00:00",
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/v1/devices/1"
    },
    "devices": {
      "href": "http://localhost:8080/api/v1/devices"
    }
  }
}
```

#### Get All Devices (Paginated)
```bash
curl http://localhost:8080/api/v1/devices?page=0&size=10
```

**Response (200 OK):**
```json
{
  "_embedded": {
    "deviceDTOList": [
      {
        "id": 1,
        "name": "iPhone 15",
        "brand": "Apple",
        "state": "available",
        "creationTime": "2023-10-01T12:00:00",
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/v1/devices/1"
          }
        }
      }
    ]
  },
  "_links": {
    "first": {
      "href": "http://localhost:8080/api/v1/devices?page=0&size=10"
    },
    "self": {
      "href": "http://localhost:8080/api/v1/devices?page=0&size=10"
    },
    "next": {
      "href": "http://localhost:8080/api/v1/devices?page=1&size=10"
    },
    "last": {
      "href": "http://localhost:8080/api/v1/devices?page=5&size=10"
    }
  },
  "page": {
    "size": 10,
    "totalElements": 50,
    "totalPages": 5,
    "number": 0
  }
}
```

#### Get Devices by Brand
```bash
curl http://localhost:8080/api/v1/devices?brand=Apple
```

#### Update Device State
```bash
curl -X PATCH http://localhost:8080/api/v1/devices/1 \
  -H "Content-Type: application/json" \
  -d '{"state": "in_use"}'
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "iPhone 15",
  "brand": "Apple",
  "state": "in_use",
  "creationTime": "2023-10-01T12:00:00",
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/v1/devices/1"
    },
    "devices": {
      "href": "http://localhost:8080/api/v1/devices"
    }
  }
}
```

## Quick Start

### Prerequisites

- **Java 21+**
- **Docker & Docker Compose**
- **Git**

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd restapi
   ```

2. **Start database dependencies**
   ```bash
   docker-compose -f docker-compose.dev.yml up -d
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

4. **Access the application**
   - API: http://localhost:8080/api/v1/devices
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - API Docs: http://localhost:8080/api-docs
   - pgAdmin: http://localhost:8081 (admin@restapi.com / admin)

### Testing

#### Run Unit Tests
```bash
./gradlew test
```

#### Run Contract Tests
```bash
./gradlew contractTest
```

#### Run All Tests
```bash
./gradlew check
```

#### Generate Code Coverage Reports
```bash
# Run tests and generate jacoco coverage report
./gradlew test jacocoTestReport

# Or simply run tests (jacocoTestReport runs automatically)
./gradlew test
```

**View Coverage Report:**
- Open `build/jacocoHtml/index.html` in your browser
- The report shows line and branch coverage for all classes
- Detailed coverage is available for each package and class

## Docker Deployment

### Development Environment
```bash
# Start only database dependencies
docker-compose -f docker-compose.dev.yml up -d
```

### Prod-like Environment
```bash
# Start database with application
docker-compose -f docker-compose.prod.yml up -d
```

### Build and Run Custom Image
```bash
# Build the application
./gradlew build

# Build Docker image
docker build -t restapi:latest .

# Run with external database
docker run -d \
  --name restapi-app \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://localhost:5432/restapi_prod \
  -e DATABASE_USERNAME=restapi_user \
  -e DATABASE_PASSWORD=restapi_password \
  restapi:latest
```

## Database Configuration

### Profiles

- **prod**: Local run with containerised application with PostgreSQL DB in a docker container 
- **local**: Local development with PostgreSQL DB in a docker container
- **test**: Testing with H2 in-memory DB

### Environment Variables

| Variable                 | Description       | Default                                        |
|--------------------------|-------------------|------------------------------------------------|
| `DATABASE_URL`           | JDBC URL          | `jdbc:postgresql://localhost:5432/restapi_dev` |
| `DATABASE_USERNAME`      | Database username | `restapi_user`                                 |
| `DATABASE_PASSWORD`      | Database password | `restapi_password`                             |
| `SPRING_PROFILES_ACTIVE` | Active profile    | `local`                                        |

### Migrations

Database schema is managed by Liquibase. Migrations are located in:
- `src/main/resources/db/changelog/`

## Validation Rules

### Business Logic Constraints

1. **Creation Time Immutability**: The `creationTime` field cannot be updated after device creation
2. **In-Use Device Restrictions**: 
   - Cannot update `name` or `brand` of devices in `IN_USE` state
   - Cannot delete devices in `IN_USE` state
3. **Required Fields**: `name`, `brand`, and `state` are mandatory
4. **State Validation**: Only `AVAILABLE`, `IN_USE`, and `INACTIVE` states are allowed

### Validation Examples

```bash
# This will fail - trying to update name of in-use device
curl -X PUT http://localhost:8080/api/v1/devices/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Name",
    "brand": "Apple", 
    "state": "in_use"
  }'

# This will fail - trying to delete in-use device
curl -X DELETE http://localhost:8080/api/v1/devices/1
```

## Monitoring & Health Checks

### Health Check Endpoint
```bash
curl http://localhost:8080/actuator/health
```

### Metrics
```bash
curl http://localhost:8080/actuator/metrics
```

## API Documentation

Interactive API documentation is available at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Development

### Project initialization

Project stub is created using [Spring Initializr](https://start.spring.io) 

### Code Quality

The project follows Spring Boot best practices:
- **Lombok** for reducing boilerplate code
- **Validation annotations** for input validation
- **Global exception handling** for consistent error responses
- **Transaction management** for data consistency
- **Comprehensive logging** for monitoring

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

This project is licensed under the MIT License.