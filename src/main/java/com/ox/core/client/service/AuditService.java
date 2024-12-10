package com.ox.core.client.service;

import com.ox.core.client.model.entity.AuditLog;
import jakarta.servlet.http.HttpServletRequest;

public interface AuditService {
    void logAuthenticationAttempt(String clientId, String abi, AuditLog.Status status, String message, HttpServletRequest request);
    void logPasswordChange(String clientId, AuditLog.Status status, String message, HttpServletRequest request);
    void logAccountLocked(String clientId, String message, HttpServletRequest request);
    void logAccountUnlocked(String clientId, String message, HttpServletRequest request);
    void logTokenGenerated(String clientId, HttpServletRequest request);
}
