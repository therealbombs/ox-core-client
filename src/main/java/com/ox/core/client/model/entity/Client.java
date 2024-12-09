package com.ox.core.client.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "CLIENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    @Id
    @Column(name = "client_id")
    private String clientId;

    @Column(nullable = false)
    private String abi;

    @Column(name = "fiscal_code", nullable = false, unique = true)
    private String fiscalCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(name = "preferred_language")
    private String preferredLanguage;

    @Column(name = "last_access")
    private LocalDateTime lastAccess;

    @Column(name = "previous_access")
    private LocalDateTime previousAccess;

    @Column(name = "failed_attempts")
    private Integer failedAttempts;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "password")
    private String password;

    @Column(name = "password_change_required")
    private Boolean passwordChangeRequired;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy = "client")
    private Set<AccountHolder> accountHolders;
}
