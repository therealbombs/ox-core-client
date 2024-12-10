package com.ox.core.client.service.impl;

import com.ox.core.client.config.SecurityProperties;
import com.ox.core.client.exception.ResourceNotFoundException;
import com.ox.core.client.model.dto.ChangePasswordRequest;
import com.ox.core.client.model.dto.ChangePasswordResponse;
import com.ox.core.client.model.entity.Client;
import com.ox.core.client.repository.ClientRepository;
import com.ox.core.client.service.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final ClientRepository clientRepository;
    private final SecurityProperties securityProperties;

    @Override
    @Transactional
    public ChangePasswordResponse changePassword(String clientId, ChangePasswordRequest request) {
        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        // Check if account is locked
        if (isAccountLocked(client)) {
            log.warn("Account is locked for client: {}", client.getClientId());
            return ChangePasswordResponse.builder()
                    .message("Account is locked")
                    .remainingAttempts(0)
                    .lockedUntil(client.getLockedUntil())
                    .passwordChangeRequired(client.getPasswordChangeRequired())
                    .build();
        }

        // Validate current password
        if (!request.getCurrentPassword().equals(client.getPassword())) {
            return handleFailedAttempt(client, "Current password is incorrect");
        }

        // Validate new password format
        if (!isPasswordValid(request.getNewPassword())) {
            return ChangePasswordResponse.builder()
                    .message("New password does not meet security requirements")
                    .remainingAttempts(getRemainingAttempts(client))
                    .passwordChangeRequired(client.getPasswordChangeRequired())
                    .build();
        }

        // Check if new password matches confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ChangePasswordResponse.builder()
                    .message("New password and confirmation do not match")
                    .remainingAttempts(getRemainingAttempts(client))
                    .passwordChangeRequired(client.getPasswordChangeRequired())
                    .build();
        }

        // Check if new password is different from current
        if (request.getNewPassword().equals(client.getPassword())) {
            return ChangePasswordResponse.builder()
                    .message("New password must be different from current password")
                    .remainingAttempts(getRemainingAttempts(client))
                    .passwordChangeRequired(client.getPasswordChangeRequired())
                    .build();
        }

        // Update password
        client.setPassword(request.getNewPassword());
        client.setPasswordChangeRequired(false);
        client.setFailedAttempts(0);
        client.setLockedUntil(null);
        clientRepository.save(client);

        log.info("Password successfully changed for client: {}", clientId);
        
        return ChangePasswordResponse.builder()
                .message("Password successfully changed")
                .remainingAttempts(securityProperties.getPassword().getMaxAttempts())
                .passwordChangeRequired(false)
                .build();
    }

    private boolean isAccountLocked(Client client) {
        return client.getLockedUntil() != null && 
               LocalDateTime.now().isBefore(client.getLockedUntil());
    }

    private boolean isPasswordValid(String password) {
        return Pattern.matches(securityProperties.getPassword().getPattern(), password);
    }

    private int getRemainingAttempts(Client client) {
        int failedAttempts = client.getFailedAttempts() == null ? 0 : client.getFailedAttempts();
        return securityProperties.getPassword().getMaxAttempts() - failedAttempts;
    }

    private ChangePasswordResponse handleFailedAttempt(Client client, String message) {
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

        return ChangePasswordResponse.builder()
                .message(message)
                .remainingAttempts(remainingAttempts)
                .lockedUntil(client.getLockedUntil())
                .passwordChangeRequired(client.getPasswordChangeRequired())
                .build();
    }
}
