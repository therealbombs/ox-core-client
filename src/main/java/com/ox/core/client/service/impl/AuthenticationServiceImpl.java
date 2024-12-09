package com.ox.core.client.service.impl;

import com.ox.core.client.model.dto.AuthenticationRequest;
import com.ox.core.client.model.dto.AuthenticationResponse;
import com.ox.core.client.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Mock implementation for demonstration
        return AuthenticationResponse.builder()
                .clientId(UUID.randomUUID().toString())
                .abi("12345")
                .token("mock.jwt.token")
                .build();
    }
}
