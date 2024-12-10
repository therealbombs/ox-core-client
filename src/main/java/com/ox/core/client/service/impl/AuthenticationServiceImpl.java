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
        try {
            var client = clientRepository.findByClientIdAndAbi(request.getClientId(), request.getAbi())
                    .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

            if (isAccountLocked(client)) {
                throw new LockedException("Account is locked");
            }

            if (!passwordEncoder.matches(request.getPassword(), client.getPassword())) {
                handleFailedAttempt(client, "Invalid credentials");
                throw new BadCredentialsException("Invalid credentials");
            }

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
            
            return AuthenticationResponse.builder()
                    .token(token)
                    .build();

        } catch (BadCredentialsException | LockedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Authentication error", e);
            throw new BadCredentialsException("Authentication failed");
        }
    }

    @Transactional
    public AuthenticationResponse unlockAccount(AuthenticationRequest request) {
        Client client = clientRepository.findByClientIdAndAbi(request.getClientId(), request.getAbi())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        // Check if account is actually locked
        if (client.getLockedUntil() == null) {
            // Account is not locked, proceed with normal authentication
            return authenticate(request);
        }

        // Check if lock duration has expired
        if (client.getLockedUntil().isAfter(LocalDateTime.now())) {
            // Account is still locked
            return AuthenticationResponse.builder()
                    .clientId(client.getClientId())
                    .abi(client.getAbi())
                    .remainingAttempts(0)
                    .lockedUntil(client.getLockedUntil())
                    .passwordChangeRequired(client.getPasswordChangeRequired())
                    .build();
        }

        // Verify credentials
        if (!passwordEncoder.matches(request.getPassword(), client.getPassword())) {
            // Invalid credentials, keep account locked
            return AuthenticationResponse.builder()
                    .clientId(client.getClientId())
                    .abi(client.getAbi())
                    .remainingAttempts(0)
                    .lockedUntil(client.getLockedUntil())
                    .passwordChangeRequired(client.getPasswordChangeRequired())
                    .build();
        }

        // Reset failed attempts and unlock account
        client.setFailedAttempts(0);
        client.setLockedUntil(null);
        clientRepository.save(client);

        // Generate token and return successful response
        String token = jwtTokenProvider.generateToken(client.getClientId(), client.getAbi());
        log.info("Account unlocked successfully for client: {}", client.getClientId());

        return AuthenticationResponse.builder()
                .clientId(client.getClientId())
                .abi(client.getAbi())
                .token(token)
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
