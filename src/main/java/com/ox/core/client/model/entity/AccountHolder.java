package com.ox.core.client.model.entity;

import com.ox.core.client.model.enums.HolderType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ACCOUNT_HOLDER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountHolder {
    @Id
    @Column(name = "holder_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String holderId;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "holder_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private HolderType holderType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
