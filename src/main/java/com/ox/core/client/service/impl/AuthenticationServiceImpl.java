package com.ox.core.client.service.impl;

import com.ox.core.client.model.dto.AuthenticationRequest;
import com.ox.core.client.model.dto.AuthenticationResponse;
import com.ox.core.client.model.entity.Client;
import com.ox.core.client.repository.ClientRepository;
import com.ox.core.client.security.JwtTokenProvider;
import com.ox.core.client.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final ClientRepository clientRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.debug("Authenticating client with username: {}", request.getUsername());
        
        // Find client by clientId (username)
        Client client = clientRepository.findByClientId(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Client not found with username: {}", request.getUsername());
                    return new BadCredentialsException("Invalid credentials");
                });

        // TODO: Add proper password validation
        // For now, we're just checking if the client exists
        
        // Update last access time
        client.setLastAccess(LocalDateTime.now());
        clientRepository.save(client);
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(client.getClientId(), client.getAbi());
        
        log.debug("Successfully authenticated client: {}", client.getClientId());
        
        return AuthenticationResponse.builder()
                .clientId(client.getClientId())
                .abi(client.getAbi())
                .token(token)
                .build();
    }
}
