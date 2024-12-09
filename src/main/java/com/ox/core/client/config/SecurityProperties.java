package com.ox.core.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    private Jwt jwt = new Jwt();
    private Password password = new Password();

    @Data
    public static class Jwt {
        private String secret;
        private long expiration;
    }

    @Data
    public static class Password {
        private String pattern;
        private int maxAttempts;
        private int lockDurationMinutes;
    }
}
