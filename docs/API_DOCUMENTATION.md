# OX Core Client API Documentation

## Overview

The OX Core Client API provides endpoints for managing bank clients and their accounts. It supports authentication, client information retrieval, and account management operations.

## Base URL

```
http://localhost:8080/api/v1
```

## Authentication

### Login

Authenticate a user and receive a JWT token.

```
POST /auth/login
```

#### Request Body

```json
{
    "username": "string",
    "password": "string"
}
```

#### Response

```json
{
    "clientId": "string",
    "abi": "string",
    "token": "string"
}
```

#### Status Codes
- 200: Success
- 401: Invalid credentials
- 400: Invalid request body

## Client API

### Get Client Details

Retrieve detailed information about a specific client.

```
GET /banks/{abi}/clients/{clientId}
```

#### Path Parameters
- `abi`: Bank ABI code
- `clientId`: Client identifier

#### Headers
- `Authorization`: Bearer token

#### Response

```json
{
    "clientId": "string",
    "abi": "string",
    "personalInfo": {
        "name": "string",
        "surname": "string",
        "fiscalCode": "string"
    },
    "preferences": {
        "language": "string",
        "lastAccess": "timestamp"
    }
}
```

#### Status Codes
- 200: Success
- 401: Unauthorized
- 404: Client not found

## Account API

### Get Client Accounts

Retrieve all accounts associated with a client.

```
GET /banks/{abi}/clients/{clientId}/accounts
```

#### Path Parameters
- `abi`: Bank ABI code
- `clientId`: Client identifier

#### Headers
- `Authorization`: Bearer token

#### Response

```json
{
    "accounts": [
        {
            "accountId": "string",
            "accountType": "enum",
            "accountNumber": "string",
            "status": "enum",
            "iban": "string",
            "holders": [
                {
                    "clientId": "string",
                    "holderType": "string",
                    "name": "string",
                    "surname": "string"
                }
            ]
        }
    ]
}
```

#### Status Codes
- 200: Success
- 401: Unauthorized
- 404: Client not found

## Data Types

### Account Type Enum
- `CURRENT_ACCOUNT`
- `DEPOSIT_ACCOUNT`
- `SECURITIES_ACCOUNT`

### Account Status Enum
- `ACTIVE`
- `TO_BE_ACTIVATED`
- `CLOSED`
- `BLOCKED`
- `OTHER`

### Holder Type Enum
- `PRIMARY`
- `SECONDARY`

## Error Responses

All error responses follow this format:

```json
{
    "status": "number",
    "message": "string",
    "timestamp": "timestamp"
}
```

### Common Error Codes
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 500: Internal Server Error

## Rate Limiting

The API implements rate limiting per client:
- 100 requests per minute per IP
- 1000 requests per hour per authenticated client

## Security

- All endpoints except `/auth/login` require authentication
- JWT tokens expire after 24 hours
- HTTPS is required in production
- Cross-Origin Resource Sharing (CORS) is configured for trusted domains only

## Best Practices

1. Always include the Authorization header with a valid JWT token
2. Handle rate limiting by implementing exponential backoff
3. Implement proper error handling for all possible response codes
4. Cache responses when appropriate
5. Use HTTPS in production environments

## API Versioning

The API version is included in the base URL path (/api/v1/). When breaking changes are introduced, a new version will be released (/api/v2/).
