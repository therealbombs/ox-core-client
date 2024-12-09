package com.ox.core.client.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChangePasswordResponse {
    private String message;
    private Integer remainingAttempts;
    private LocalDateTime lockedUntil;
    private Boolean passwordChangeRequired;
}
