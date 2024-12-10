package com.ox.core.client.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
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

    @NotBlank(message = "Client ID is required")
    @Schema(
        description = "Unique identifier for the client",
        example = "C001",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String clientId;

    @NotBlank(message = "ABI code is required")
    @Schema(
        description = "ABI (Bank identification) code",
        example = "01234",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String abi;

    @NotBlank(message = "Password is required")
    @Schema(
        description = "Client's password. Must meet password policy requirements",
        example = "Password123!",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;

    @Override
    public String toString() {
        return "AuthenticationRequest{" +
                "clientId='" + clientId + '\'' +
                ", abi='" + abi + '\'' +
                '}';
    }
}
