package com.ox.core.client.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {
    private String clientId;
    private String abi;
    private String token;
}
