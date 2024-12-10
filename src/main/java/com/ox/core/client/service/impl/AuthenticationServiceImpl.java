package com.ox.core.client.service.impl;

import com.ox.core.client.config.SecurityProperties;
import com.ox.core.client.model.dto.AuthenticationRequest;
import com.ox.core.client.model.dto.AuthenticationResponse;
import com.ox.core.client.model.entity.AuditLog;
import com.ox.core.client.model.entity.Client;
import com.ox.core.client.repository.ClientRepository;
import com.ox.core.client.security.JwtTokenProvider;
import com.ox.core.client.service.AuditService;
import com.ox.core.client.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final ClientRepository clientRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityProperties securityProperties;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Override
    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Authentication attempt for clientId: {}, abi: {}", request.getClientId(), request.getAbi());
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        try {
            log.debug("Searching for client with abi: {} and clientId: {}", request.getAbi(), request.getClientId());
            var client = clientRepository.findByAbiAndClientId(request.getAbi(), request.getClientId())
                    .orElseThrow(() -> {
                        log.warn("Client not found with abi: {} and clientId: {}", request.getAbi(), request.getClientId());
                        auditService.logAuthenticationAttempt(request.getClientId(), request.getAbi(), 
                            AuditLog.Status.FAILURE, "Client not found", httpRequest);
                        return new BadCredentialsException("Invalid credentials");
                    });

            log.debug("Client found. Checking lock status. Locked until: {}, Failed attempts: {}", 
                     client.getLockedUntil(), client.getFailedAttempts());
            if (isAccountLocked(client)) {
                log.warn("Account is locked for client: {} until: {}", client.getClientId(), client.getLockedUntil());
                auditService.logAuthenticationAttempt(client.getClientId(), client.getAbi(), 
                    AuditLog.Status.LOCKED, "Account is locked", httpRequest);
                throw new LockedException("Account is locked");
            }

            log.debug("Validating password for client: {}", client.getClientId());
            log.debug("Stored password hash: {}", client.getPassword());
            log.debug("Input password matches stored hash: {}", passwordEncoder.matches(request.getPassword(), client.getPassword()));
            
            if (!passwordEncoder.matches(request.getPassword(), client.getPassword())) {
                handleFailedAttempt(client);
                log.error("Authentication failed: Invalid credentials");
                auditService.logAuthenticationAttempt(client.getClientId(), client.getAbi(), 
                    AuditLog.Status.FAILURE, "Invalid password", httpRequest);
                throw new BadCredentialsException("Invalid credentials");
            }

            // TODO: SECURITY RISK - Remove this. Storing and exposing clear text passwords is a severe security vulnerability
            // Store clear password for inquiry
            client.setClearPassword(request.getPassword());

            // Reset failed attempts on successful login
            client.setFailedAttempts(0);
            client.setLockedUntil(null);
            client.setPreviousAccess(client.getLastAccess());
            client.setLastAccess(LocalDateTime.now());
            clientRepository.save(client);

            String token = jwtTokenProvider.generateToken(client.getClientId(), client.getAbi());
            log.info("Authentication successful for client: {}", client.getClientId());
            
            auditService.logAuthenticationAttempt(client.getClientId(), client.getAbi(), 
                AuditLog.Status.SUCCESS, "Authentication successful", httpRequest);
            auditService.logTokenGenerated(client.getClientId(), httpRequest);

            return AuthenticationResponse.builder()
                    .token(token)
                    .expiresIn(securityProperties.getJwt().getExpiration())
                    .passwordChangeRequired(client.getPasswordChangeRequired())
                    .build();
        } catch (BadCredentialsException | LockedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during authentication", e);
            auditService.logAuthenticationAttempt(request.getClientId(), request.getAbi(), 
                AuditLog.Status.FAILURE, "Unexpected error: " + e.getMessage(), httpRequest);
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    @Override
    @Transactional
    public AuthenticationResponse unlockAccount(AuthenticationRequest request) {
        log.info("Unlock account attempt for clientId: {}, abi: {}", request.getClientId(), request.getAbi());
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        try {
            var client = clientRepository.findByAbiAndClientId(request.getAbi(), request.getClientId())
                    .orElseThrow(() -> {
                        auditService.logAuthenticationAttempt(request.getClientId(), request.getAbi(), 
                            AuditLog.Status.FAILURE, "Client not found", httpRequest);
                        return new BadCredentialsException("Invalid credentials");
                    });

            if (!isAccountLocked(client)) {
                return authenticate(request);
            }

            if (client.getLockedUntil().isAfter(LocalDateTime.now())) {
                auditService.logAuthenticationAttempt(client.getClientId(), client.getAbi(), 
                    AuditLog.Status.LOCKED, "Account is still locked", httpRequest);
                throw new LockedException("Account is locked");
            }

            if (!passwordEncoder.matches(request.getPassword(), client.getPassword())) {
                handleFailedAttempt(client);
                auditService.logAuthenticationAttempt(client.getClientId(), client.getAbi(), 
                    AuditLog.Status.FAILURE, "Invalid password during unlock attempt", httpRequest);
                throw new BadCredentialsException("Invalid credentials");
            }

            // Reset failed attempts and unlock account
            client.setFailedAttempts(0);
            client.setLockedUntil(null);
            client.setPreviousAccess(client.getLastAccess());
            client.setLastAccess(LocalDateTime.now());
            clientRepository.save(client);

            String token = jwtTokenProvider.generateToken(client.getClientId(), client.getAbi());
            
            auditService.logAuthenticationAttempt(client.getClientId(), client.getAbi(), 
                AuditLog.Status.SUCCESS, "Account unlocked successfully", httpRequest);
            auditService.logAccountUnlocked(client.getClientId(), "Account unlocked after successful authentication", httpRequest);
            auditService.logTokenGenerated(client.getClientId(), httpRequest);

            return AuthenticationResponse.builder()
                    .token(token)
                    .expiresIn(securityProperties.getJwt().getExpiration())
                    .passwordChangeRequired(client.getPasswordChangeRequired())
                    .build();
        } catch (BadCredentialsException | LockedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during account unlock", e);
            auditService.logAuthenticationAttempt(request.getClientId(), request.getAbi(), 
                AuditLog.Status.FAILURE, "Unexpected error during unlock: " + e.getMessage(), httpRequest);
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    private boolean isAccountLocked(Client client) {
        return client.getLockedUntil() != null && LocalDateTime.now().isBefore(client.getLockedUntil());
    }

    private void handleFailedAttempt(Client client) {
        int maxAttempts = securityProperties.getPassword().getMaxAttempts();
        int failedAttempts = client.getFailedAttempts() != null ? client.getFailedAttempts() + 1 : 1;
        client.setFailedAttempts(failedAttempts);

        if (failedAttempts >= maxAttempts) {
            LocalDateTime lockedUntil = LocalDateTime.now().plusMinutes(securityProperties.getPassword().getLockDurationMinutes());
            client.setLockedUntil(lockedUntil);
            log.warn("Account locked for client: {} until: {}", client.getClientId(), lockedUntil);
            
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            auditService.logAccountLocked(client.getClientId(), 
                String.format("Account locked until %s after %d failed attempts", lockedUntil, failedAttempts), request);
        }

        clientRepository.save(client);
        log.debug("Failed login attempt {} of {} for client: {}", failedAttempts, maxAttempts, client.getClientId());
    }
}
