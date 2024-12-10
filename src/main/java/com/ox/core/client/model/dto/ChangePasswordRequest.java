package com.ox.core.client.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"currentPassword", "newPassword", "confirmPassword"})
public class ChangePasswordRequest {
    @NotBlank(message = "Current password is required")
    @JsonProperty(access = Access.WRITE_ONLY)
    @ToString.Exclude
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @JsonProperty(access = Access.WRITE_ONLY)
    @ToString.Exclude
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    @JsonProperty(access = Access.WRITE_ONLY)
    @ToString.Exclude
    private String confirmPassword;
}
