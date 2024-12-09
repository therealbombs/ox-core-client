package com.ox.core.client.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClientResponse {
    private String clientId;
    private String abi;
    private PersonalInfo personalInfo;
    private Preferences preferences;
    private AuditInfo auditInfo;

    @Data
    @Builder
    public static class PersonalInfo {
        private String name;
        private String surname;
        private String fiscalCode;
    }

    @Data
    @Builder
    public static class Preferences {
        private String language;
        private LocalDateTime lastAccess;
    }

    @Data
    @Builder
    public static class AuditInfo {
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }
}
