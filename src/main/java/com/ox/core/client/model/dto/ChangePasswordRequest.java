package com.ox.core.client.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Current password is required")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String confirmPassword;

    @Override
    public String toString() {
        return "ChangePasswordRequest{<REDACTED>}";
    }
}
