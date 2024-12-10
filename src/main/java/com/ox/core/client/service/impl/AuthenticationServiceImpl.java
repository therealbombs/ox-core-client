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
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Authentication attempt for clientId: {}, abi: {}", request.getClientId(), request.getAbi());
        try {
            log.debug("Searching for client with abi: {} and clientId: {}", request.getAbi(), request.getClientId());
            var client = clientRepository.findByAbiAndClientId(request.getAbi(), request.getClientId())
                    .orElseThrow(() -> {
                        log.warn("Client not found with abi: {} and clientId: {}", request.getAbi(), request.getClientId());
                        return new BadCredentialsException("Invalid credentials");
                    });

            log.debug("Client found. Checking lock status. Locked until: {}, Failed attempts: {}", 
                     client.getLockedUntil(), client.getFailedAttempts());
            if (isAccountLocked(client)) {
                log.warn("Account is locked for client: {} until: {}", client.getClientId(), client.getLockedUntil());
                throw new LockedException("Account is locked");
            }

            log.debug("Validating password for client: {}", client.getClientId());
            log.debug("Stored password hash: {}", client.getPassword());
            log.debug("Input password matches stored hash: {}", passwordEncoder.matches(request.getPassword(), client.getPassword()));
            
            if (!passwordEncoder.matches(request.getPassword(), client.getPassword())) {
                log.warn("Invalid password attempt for client: {}", client.getClientId());
                handleFailedAttempt(client, "Invalid credentials");
                throw new BadCredentialsException("Invalid credentials");
            }

            log.info("Authentication successful for client: {}", client.getClientId());
            // Reset failed attempts on successful login
            if (client.getFailedAttempts() > 0) {
                client.setFailedAttempts(0);
                client.setLockedUntil(null);
                clientRepository.save(client);
            }

            // Update access times
            LocalDateTime now = LocalDateTime.now();
            client.setPreviousAccess(client.getLastAccess());
            client.setLastAccess(now);
            clientRepository.save(client);

            String token = jwtTokenProvider.generateToken(client.getClientId(), client.getAbi());
            log.debug("Generated JWT token for client: {}", client.getClientId());
            return AuthenticationResponse.builder()
                    .token(token)
                    .build();

        } catch (BadCredentialsException | LockedException e) {
            log.error("Authentication failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during authentication", e);
            throw new BadCredentialsException("Authentication failed");
        }
    }

    @Transactional
    public AuthenticationResponse unlockAccount(AuthenticationRequest request) {
        log.info("Unlock account attempt for clientId: {}, abi: {}", request.getClientId(), request.getAbi());
        Client client = clientRepository.findByAbiAndClientId(request.getAbi(), request.getClientId())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        log.debug("Client found. Checking lock status. Locked until: {}, Failed attempts: {}", 
                 client.getLockedUntil(), client.getFailedAttempts());
        // Check if account is actually locked
        if (client.getLockedUntil() == null) {
            log.info("Account is not locked for client: {}", client.getClientId());
            // Account is not locked, proceed with normal authentication
            return authenticate(request);
        }

        log.debug("Account is locked for client: {} until: {}", client.getClientId(), client.getLockedUntil());
        // Check if lock duration has expired
        if (client.getLockedUntil().isAfter(LocalDateTime.now())) {
            log.warn("Account is still locked for client: {} until: {}", client.getClientId(), client.getLockedUntil());
            // Account is still locked
            return AuthenticationResponse.builder()
                    .clientId(client.getClientId())
                    .abi(client.getAbi())
                    .remainingAttempts(0)
                    .lockedUntil(client.getLockedUntil())
                    .passwordChangeRequired(client.getPasswordChangeRequired())
                    .build();
        }

        log.info("Lock duration has expired for client: {}", client.getClientId());
        // Verify credentials
        log.debug("Validating password for client: {}", client.getClientId());
        log.debug("Stored password hash: {}", client.getPassword());
        log.debug("Input password matches stored hash: {}", passwordEncoder.matches(request.getPassword(), client.getPassword()));
        
        if (!passwordEncoder.matches(request.getPassword(), client.getPassword())) {
            log.warn("Invalid password attempt for client: {}", client.getClientId());
            // Invalid credentials, keep account locked
            return AuthenticationResponse.builder()
                    .clientId(client.getClientId())
                    .abi(client.getAbi())
                    .remainingAttempts(0)
                    .lockedUntil(client.getLockedUntil())
                    .passwordChangeRequired(client.getPasswordChangeRequired())
                    .build();
        }

        log.info("Credentials are valid for client: {}", client.getClientId());
        // Reset failed attempts and unlock account
        client.setFailedAttempts(0);
        client.setLockedUntil(null);
        clientRepository.save(client);

        log.info("Account unlocked successfully for client: {}", client.getClientId());
        // Generate token and return successful response
        String token = jwtTokenProvider.generateToken(client.getClientId(), client.getAbi());
        log.debug("Generated JWT token for client: {}", client.getClientId());
        return AuthenticationResponse.builder()
                .clientId(client.getClientId())
                .abi(client.getAbi())
                .token(token)
                .remainingAttempts(securityProperties.getPassword().getMaxAttempts())
                .passwordChangeRequired(client.getPasswordChangeRequired())
                .build();
    }

    private boolean isAccountLocked(Client client) {
        log.debug("Checking if account is locked for client: {}", client.getClientId());
        return client.getLockedUntil() != null && 
               LocalDateTime.now().isBefore(client.getLockedUntil());
    }

    private boolean isPasswordValid(String password) {
        log.debug("Checking if password is valid: {}", password);
        return Pattern.matches(securityProperties.getPassword().getPattern(), password);
    }

    private AuthenticationResponse handleFailedAttempt(Client client, String message) {
        log.info("Handling failed attempt for client: {}", client.getClientId());
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
