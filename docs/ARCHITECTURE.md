# OX Core Client Architecture Documentation

## Overview

The OX Core Client is a Spring Boot microservice designed to manage bank clients and their accounts. It follows a layered architecture pattern with clear separation of concerns.

## Architecture Layers

### 1. Presentation Layer (Controllers)
- Handles HTTP requests and responses
- Input validation
- Response formatting
- Error handling
- API documentation
- Rate limiting

Key Components:
- `AuthenticationController`: Handles user authentication
- `ClientController`: Manages client-related operations
- `AccountController`: Handles account-related operations

### 2. Service Layer
- Business logic implementation
- Transaction management
- Data transformation
- Security rules
- Business validation

Key Components:
- `AuthenticationService`: Authentication logic and JWT token generation
- `ClientService`: Client management operations
- `AccountService`: Account management operations

### 3. Repository Layer
- Data access abstraction
- Database operations
- Query optimization
- Data persistence

Key Components:
- `ClientRepository`: Client data access
- `AccountRepository`: Account data access
- `AccountHolderRepository`: Account holder relationship management

### 4. Domain Layer
- Business entities
- Value objects
- Domain events
- Business rules

Key Components:
- `Client`: Client entity
- `Account`: Account entity
- `AccountHolder`: Account-Client relationship entity
- Various enums for type safety

## Cross-Cutting Concerns

### 1. Security
- JWT-based authentication
- Role-based access control
- Request filtering
- Token validation
- Password encryption

Components:
- `JwtTokenProvider`
- `JwtAuthenticationFilter`
- `SecurityConfig`

### 2. Error Handling
- Global exception handling
- Custom exceptions
- Error response standardization
- Logging

Components:
- `GlobalExceptionHandler`
- `ResourceNotFoundException`
- Custom exception classes

### 3. Validation
- Input validation
- Business rule validation
- Data consistency checks

Components:
- Jakarta Validation annotations
- Custom validators
- Validation groups

## Database Design

### Tables
1. CLIENT
   - Primary key: client_id
   - Indexes: abi, fiscal_code
   - Audit fields: created_at, modified_at

2. ACCOUNT
   - Primary key: account_id
   - Indexes: abi, account_number, iban
   - Audit fields: created_at, modified_at

3. ACCOUNT_HOLDER
   - Primary key: holder_id
   - Foreign keys: account_id, client_id
   - Audit fields: created_at

### Relationships
- One-to-Many: Client to AccountHolder
- One-to-Many: Account to AccountHolder
- Many-to-Many: Client to Account (through AccountHolder)

## Technical Stack

### Core Framework
- Spring Boot 3.2.0
- Java 17
- Maven

### Database
- H2 Database (Development)
- JPA/Hibernate
- Spring Data JPA

### Security
- Spring Security
- JWT Authentication
- BCrypt Password Encoding

### Documentation
- SpringDoc OpenAPI
- Swagger UI

### Testing
- JUnit 5
- Mockito
- Spring Boot Test
- H2 In-Memory Database

## Design Patterns

1. Repository Pattern
   - Data access abstraction
   - Clean separation from business logic

2. Builder Pattern
   - Used in DTOs and entities
   - Implemented via Lombok

3. DTO Pattern
   - Clean API responses
   - Data transformation
   - API versioning support

4. Factory Pattern
   - Object creation
   - Test data generation

## Performance Considerations

1. Database
   - Proper indexing
   - Query optimization
   - Connection pooling
   - Lazy loading where appropriate

2. Caching
   - Response caching
   - Entity caching
   - Cache invalidation strategies

3. API Performance
   - Pagination support
   - Rate limiting
   - Async operations where applicable

## Scalability

1. Horizontal Scaling
   - Stateless design
   - Load balancer ready
   - Session management via JWT

2. Vertical Scaling
   - Connection pool sizing
   - JVM tuning
   - Database optimization

## Monitoring and Observability

1. Logging
   - Structured logging
   - Log levels
   - Error tracking

2. Metrics
   - API metrics
   - Database metrics
   - Performance metrics

3. Health Checks
   - Application health
   - Database health
   - External dependencies

## Deployment

1. Containerization
   - Docker support
   - Environment configuration
   - Resource limits

2. Configuration
   - External configuration
   - Environment-specific settings
   - Secrets management

## Future Considerations

1. Potential Improvements
   - Cache implementation
   - Event sourcing
   - Message queues
   - Real-time updates

2. Scalability Enhancements
   - Microservices split
   - Database sharding
   - Read replicas

3. Additional Features
   - Batch operations
   - Reporting capabilities
   - Audit logging
   - Multi-tenancy support
