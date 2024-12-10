package com.ox.core.client.repository;

import com.ox.core.client.model.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByClientIdOrderByCreatedAtDesc(String clientId);
    List<AuditLog> findByEventTypeOrderByCreatedAtDesc(AuditLog.EventType eventType);
    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
    List<AuditLog> findByClientIdAndEventTypeOrderByCreatedAtDesc(String clientId, AuditLog.EventType eventType);
}
