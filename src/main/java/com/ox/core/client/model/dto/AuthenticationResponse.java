package com.ox.core.client.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuthenticationResponse {
    private String clientId;
    private String abi;
    private String token;
    private Integer remainingAttempts;
    private LocalDateTime lockedUntil;
}
