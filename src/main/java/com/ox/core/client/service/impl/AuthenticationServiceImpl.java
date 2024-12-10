package com.ox.core.client.service.impl;

import com.ox.core.client.config.SecurityProperties;
import com.ox.core.client.model.dto.AuthenticationRequest;
import com.ox.core.client.model.dto.AuthenticationResponse;
import com.ox.core.client.model.entity.Client;
import com.ox.core.client.repository.ClientRepository;
import com.ox.core.client.security.JwtTokenProvider;
import com.ox.core.client.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final ClientRepository clientRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityProperties securityProperties;
    private final PasswordEncoder passwordEncoder;

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

        // Check if account is locked
        if (isAccountLocked(client)) {
            log.warn("Account is locked for client: {}", client.getClientId());
            return AuthenticationResponse.builder()
                    .clientId(client.getClientId())
                    .abi(client.getAbi())
                    .lockedUntil(client.getLockedUntil())
                    .remainingAttempts(0)
                    .passwordChangeRequired(client.getPasswordChangeRequired())
                    .build();
        }

        // Validate password format
        if (!isPasswordValid(request.getPassword())) {
            log.warn("Invalid password format for client: {}", client.getClientId());
            return handleFailedAttempt(client, "Invalid password format");
        }

        // Validate credentials
        if (!passwordEncoder.matches(request.getPassword(), client.getPassword())) {
            log.warn("Invalid password for client: {}", client.getClientId());
            return handleFailedAttempt(client, "Invalid credentials");
        }

        // Reset failed attempts on successful login
        client.setFailedAttempts(0);
        client.setLockedUntil(null);
        
        // Update access times
        LocalDateTime now = LocalDateTime.now();
        client.setPreviousAccess(client.getLastAccess());
        client.setLastAccess(now);
        clientRepository.save(client);
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(client.getClientId(), client.getAbi());
        log.debug("Generated token for client: {}", client.getClientId());
        
        return AuthenticationResponse.builder()
                .clientId(client.getClientId())
                .abi(client.getAbi())
                .token(token)  // Return just the token, client will add "Bearer" prefix
                .remainingAttempts(securityProperties.getPassword().getMaxAttempts())
                .passwordChangeRequired(client.getPasswordChangeRequired())
                .build();
    }

    private boolean isAccountLocked(Client client) {
        return client.getLockedUntil() != null && 
               LocalDateTime.now().isBefore(client.getLockedUntil());
    }

    private boolean isPasswordValid(String password) {
        return Pattern.matches(securityProperties.getPassword().getPattern(), password);
    }

    private AuthenticationResponse handleFailedAttempt(Client client, String message) {
        int failedAttempts = client.getFailedAttempts() == null ? 0 : client.getFailedAttempts();
        failedAttempts++;
        client.setFailedAttempts(failedAttempts);

        int maxAttempts = securityProperties.getPassword().getMaxAttempts();
        int remainingAttempts = maxAttempts - failedAttempts;

        if (failedAttempts >= maxAttempts) {
            LocalDateTime lockedUntil = LocalDateTime.now()
                    .plusMinutes(securityProperties.getPassword().getLockDurationMinutes());
            client.setLockedUntil(lockedUntil);
            remainingAttempts = 0;
            log.warn("Account locked for client: {} until: {}", client.getClientId(), lockedUntil);
        }

        clientRepository.save(client);

        return AuthenticationResponse.builder()
                .clientId(client.getClientId())
                .abi(client.getAbi())
                .remainingAttempts(remainingAttempts)
                .lockedUntil(client.getLockedUntil())
                .passwordChangeRequired(client.getPasswordChangeRequired())
                .build();
    }
}
