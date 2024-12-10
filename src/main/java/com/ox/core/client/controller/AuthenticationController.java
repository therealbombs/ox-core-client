package com.ox.core.client.controller;

import com.ox.core.client.model.dto.AuthenticationRequest;
import com.ox.core.client.model.dto.AuthenticationResponse;
import com.ox.core.client.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication related endpoints")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(
        summary = "Authenticate client",
        description = "Authenticate a client using their credentials. Returns a JWT token on successful authentication. " +
                     "The token should be used in subsequent API calls in the Authorization header as 'Bearer <token>'. " +
                     "Failed login attempts are tracked and may result in account lockout."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully authenticated",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AuthenticationResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "clientId": "C001",
                        "abi": "01234",
                        "token": "eyJhbGciOiJIUzI1NiJ9...",
                        "remainingAttempts": 3,
                        "passwordChangeRequired": false,
                        "lockedUntil": null
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = """
                    {
                        "clientId": "C001",
                        "abi": "01234",
                        "remainingAttempts": 2,
                        "passwordChangeRequired": false,
                        "lockedUntil": null
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "423",
            description = "Account is locked due to too many failed attempts",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = """
                    {
                        "clientId": "C001",
                        "abi": "01234",
                        "remainingAttempts": 0,
                        "passwordChangeRequired": false,
                        "lockedUntil": "2024-12-10T12:00:00"
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request format or missing required fields",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = """
                    {
                        "timestamp": "2024-12-10T11:01:26+01:00",
                        "status": 400,
                        "error": "Bad Request",
                        "message": "Invalid request format",
                        "path": "/api/v1/auth/login"
                    }
                    """)
            )
        )
    })
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        AuthenticationResponse response = authenticationService.authenticate(request);
        
        // Account is locked
        if (response.getLockedUntil() != null) {
            return ResponseEntity.status(HttpStatus.LOCKED).body(response);
        }
        
        // Authentication failed (invalid credentials or password format)
        if (response.getToken() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        // Successfully authenticated
        return ResponseEntity.ok(response);
    }

    @PostMapping("/unlock")
    @Operation(
        summary = "Unlock client account",
        description = "Attempts to unlock a locked client account. The account will only be unlocked if:\n" +
                     "1. The lock duration has expired\n" +
                     "2. The provided credentials are correct\n" +
                     "If unlocking is successful, the account's failed attempts counter will be reset."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Account successfully unlocked",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AuthenticationResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "clientId": "C001",
                        "abi": "01234",
                        "token": "eyJhbGciOiJIUzI1NiJ9...",
                        "remainingAttempts": 3,
                        "passwordChangeRequired": false,
                        "lockedUntil": null
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = """
                    {
                        "clientId": "C001",
                        "abi": "01234",
                        "remainingAttempts": 0,
                        "passwordChangeRequired": false,
                        "lockedUntil": "2024-12-10T11:36:21"
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request format or missing required fields"
        )
    })
    public ResponseEntity<AuthenticationResponse> unlockAccount(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        AuthenticationResponse response = authenticationService.unlockAccount(request);
        
        if (response.getToken() != null) {
            // Account was successfully unlocked and authenticated
            return ResponseEntity.ok(response);
        } else {
            // Account remains locked or credentials were invalid
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
