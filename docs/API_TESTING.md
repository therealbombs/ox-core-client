# API Testing Guide

This guide explains how to test the OX Core Client APIs using the provided Postman collection.

## Prerequisites

1. Install [Postman](https://www.postman.com/downloads/)
2. Import the following files into Postman:
   - `OX_Core_Client_API.postman_collection.json`
   - `OX_Core_Client_Environment.postman_environment.json`

## Environment Setup

1. In Postman, select the "OX Core Client - Local" environment from the environment dropdown
2. The environment contains the following variables:
   - `baseUrl`: The base URL for the API (default: http://localhost:8080/api/v1)
   - `token`: JWT token (automatically set after login)
   - `testAbi`: Test bank ABI (default: 01234)
   - `testClientId`: Test client ID (default: C001)

## Test Data

The collection uses the sample data provided in `data.sql`, which includes:

### Clients
1. Mario Rossi (C001)
   - ABI: 01234
   - Fiscal Code: RSSMRA80A01H501A
   - Password: password
2. Luigi Verdi (C002)
   - ABI: 01234
   - Fiscal Code: VRDLGU85M15H501B
   - Password: password
3. Giovanna Bianchi (C003)
   - ABI: 56789
   - Fiscal Code: BNCGNN90D45H501C
   - Password: password
4. Francesco Rossi (C004)
   - ABI: 90123
   - Fiscal Code: RSSMRA80A01H501D
   - Password: password

### Test Scenarios

1. Authentication
   - Login with valid credentials
   - Login with invalid credentials (tracks failed attempts)
   - Account lockout after multiple failed attempts
   - Account unlock with correct credentials
   - Access protected endpoints without token
   - Access protected endpoints with invalid token

2. Client Operations
   - Get client details for existing client
   - Get client details for non-existent client
   - Get client details with wrong ABI

3. Account Operations
   - Get accounts for client with multiple accounts
   - Get accounts for client with no accounts
   - Get accounts with wrong client ID
   - Get accounts with wrong ABI

## Running the Tests

1. Start the OX Core Client application
2. In Postman, select the "OX Core Client API" collection
3. Execute the requests in the following order:

   a. Authentication:
      - POST /auth/login (saves token automatically)
      - POST /auth/unlock (if account is locked)
   
   b. Client Operations:
      - GET /banks/{abi}/clients/{clientId}
   
   c. Account Operations:
      - GET /banks/{abi}/clients/{clientId}/accounts

## Response Examples

### Successful Login Response
```json
{
    "clientId": "C001",
    "abi": "01234",
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 3600,
    "remainingAttempts": 3,
    "passwordChangeRequired": false
}
```

### Failed Login Response (401 Unauthorized)
```json
{
    "clientId": "C001",
    "abi": "01234",
    "remainingAttempts": 2,
    "passwordChangeRequired": false,
    "lockedUntil": null
}
```

### Account Locked Response (423 Locked)
```json
{
    "clientId": "C001",
    "abi": "01234",
    "remainingAttempts": 0,
    "passwordChangeRequired": false,
    "lockedUntil": "2024-12-10T12:00:00"
}
```

### Client Details Response
```json
{
    "clientId": "C001",
    "abi": "01234",
    "personalInfo": {
        "name": "Mario",
        "surname": "Rossi",
        "fiscalCode": "RSSMRA80A01H501A"
    },
    "preferences": {
        "language": "it",
        "lastAccess": "2024-12-09T21:48:38"
    }
}
```

### Client Accounts Response
```json
{
    "accounts": [
        {
            "accountId": "A001",
            "accountType": "CURRENT_ACCOUNT",
            "accountNumber": "CA001",
            "status": "ACTIVE",
            "iban": "IT01234000000CA001",
            "holders": [
                {
                    "clientId": "C001",
                    "holderType": "PRIMARY",
                    "name": "Mario",
                    "surname": "Rossi"
                }
            ]
        }
    ]
}
```

## Error Responses

### 400 Bad Request
```json
{
    "timestamp": "2024-12-10T11:01:26+01:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid request format",
    "path": "/auth/login"
}
```

### 401 Unauthorized
```json
{
    "status": 401,
    "message": "Invalid credentials",
    "timestamp": "2024-12-09T21:48:38"
}
```

### 404 Not Found
```json
{
    "status": 404,
    "message": "Client not found",
    "timestamp": "2024-12-09T21:48:38"
}
```

## Troubleshooting

1. If you get a 401 error, ensure:
   - You've executed the login request first
   - The token is being correctly set in the environment
   - The token hasn't expired (default expiration is 1 hour)

2. If you get a 404 error, verify:
   - The ABI and client ID match the test data
   - The application has started correctly
   - The database has been initialized with the test data

3. If your account gets locked:
   - Wait for the lock duration to expire (default 15 minutes)
   - Use the /auth/unlock endpoint with correct credentials to unlock
   - Reset the application to clear all locks

4. For connection errors:
   - Verify the application is running
   - Check the baseUrl in the environment matches your application configuration
   - Ensure no firewall is blocking the connection
