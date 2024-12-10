package com.ox.core.client.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response object containing authentication result and relevant client information")
public class AuthenticationResponse {

    @Schema(
        description = "Client identifier",
        example = "C001"
    )
    private String clientId;

    @Schema(
        description = "ABI (Bank identification) code",
        example = "01234"
    )
    private String abi;

    @Schema(
        description = "JWT token for authenticated sessions. Only present on successful authentication.",
        example = "eyJhbGciOiJIUzI1NiJ9..."
    )
    private String token;

    @Schema(
        description = "Token expiration time in seconds",
        example = "3600"
    )
    private Long expiresIn;

    @Schema(
        description = "Number of login attempts remaining before account lockout",
        example = "2",
        minimum = "0",
        maximum = "3"
    )
    private Integer remainingAttempts;

    @Schema(
        description = "Timestamp until which the account is locked. Only present when account is locked.",
        example = "2024-12-10T12:00:00"
    )
    private LocalDateTime lockedUntil;

    @Schema(
        description = "Indicates if the client needs to change their password",
        example = "true"
    )
    private Boolean passwordChangeRequired;
}
