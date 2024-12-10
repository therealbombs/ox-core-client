package com.ox.core.client.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for client authentication")
public class AuthenticationRequest {

    @NotBlank(message = "Username is required")
    @Schema(
        description = "Username (Client ID)",
        example = "C001",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(
        description = "Client's password",
        example = "Password123!",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
}
