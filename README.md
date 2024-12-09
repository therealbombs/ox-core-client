# OX Core Client Microservice

A Spring Boot microservice for managing bank clients and their accounts.

## Technical Stack

- Spring Boot 3.2.0
- Java 17
- Maven
- H2 Database (for development)
- JUnit 5 + Mockito
- SpringDoc OpenAPI

## Prerequisites

- JDK 17
- Maven 3.8+
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code)

## Getting Started

1. Clone the repository:
```bash
git clone [repository-url]
cd ox-core-client
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on port 8080.

## API Documentation

Once the application is running, you can access:
- Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
- OpenAPI Docs: http://localhost:8080/api/v1/api-docs

## Database

The application uses H2 in-memory database for development:
- H2 Console: http://localhost:8080/api/v1/h2-console
- JDBC URL: jdbc:h2:mem:oxcoredb
- Username: sa
- Password: password

## Available APIs

### Authentication
- POST /auth/login - Authenticate user and get JWT token

### Client Management
- GET /banks/{abi}/clients/{clientId} - Get client details

### Account Management
- GET /banks/{abi}/clients/{clientId}/accounts - Get client accounts

## Testing

Run tests using:
```bash
mvn test
```

## Security

The application uses JWT for authentication. All endpoints except /auth/login require a valid JWT token in the Authorization header:
```
Authorization: Bearer [token]
```

## Development

The project follows standard Spring Boot project structure:
```
src/
├── main/
│   ├── java/
│   │   └── com/ox/core/client/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── service/
│   │       ├── exception/
│   │       └── security/
│   └── resources/
└── test/
```

## Contributing

1. Create a feature branch
2. Commit your changes
3. Push to the branch
4. Create a Pull Request

## License

This project is licensed under the MIT License.
