package com.ox.core.client.service.impl;

import com.ox.core.client.model.entity.AuditLog;
import com.ox.core.client.repository.AuditLogRepository;
import com.ox.core.client.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void logAuthenticationAttempt(String clientId, String abi, AuditLog.Status status, String message, HttpServletRequest request) {
        createAuditLog(AuditLog.EventType.LOGIN_ATTEMPT, clientId, abi, status, message, request);
    }

    @Override
    public void logPasswordChange(String clientId, AuditLog.Status status, String message, HttpServletRequest request) {
        createAuditLog(AuditLog.EventType.PASSWORD_CHANGE, clientId, null, status, message, request);
    }

    @Override
    public void logAccountLocked(String clientId, String message, HttpServletRequest request) {
        createAuditLog(AuditLog.EventType.ACCOUNT_LOCKED, clientId, null, AuditLog.Status.LOCKED, message, request);
    }

    @Override
    public void logAccountUnlocked(String clientId, String message, HttpServletRequest request) {
        createAuditLog(AuditLog.EventType.ACCOUNT_UNLOCKED, clientId, null, AuditLog.Status.SUCCESS, message, request);
    }

    @Override
    public void logTokenGenerated(String clientId, HttpServletRequest request) {
        createAuditLog(AuditLog.EventType.TOKEN_GENERATED, clientId, null, AuditLog.Status.SUCCESS, "JWT token generated", request);
    }

    private void createAuditLog(AuditLog.EventType eventType, String clientId, String abi, AuditLog.Status status, 
                              String message, HttpServletRequest request) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .eventType(eventType)
                    .clientId(clientId)
                    .abi(abi)
                    .status(status)
                    .message(message)
                    .ipAddress(getClientIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Audit log created: {}", auditLog);
        } catch (Exception e) {
            log.error("Failed to create audit log", e);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
