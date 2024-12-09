package com.ox.core.client.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${security.jwt.secret}")
    private String secretKey;

    @Value("${security.jwt.expiration}")
    private long jwtExpiration;

    private Key getSigningKey() {
        try {
            log.debug("Generating signing key");
            byte[] keyBytes = Base64.getDecoder().decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.error("Error generating signing key: {}", e.getMessage());
            throw new RuntimeException("Error generating JWT signing key", e);
        }
    }

    public String generateToken(String clientId, String abi) {
        try {
            log.debug("Generating token for client: {} with ABI: {}", clientId, abi);
            Map<String, Object> claims = new HashMap<>();
            claims.put("abi", abi);
            
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(clientId)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(getSigningKey())
                    .compact();
            
            log.debug("Token generated successfully");
            return token;
        } catch (Exception e) {
            log.error("Error generating token: {}", e.getMessage());
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    public String extractClientId(String token) {
        try {
            log.debug("Extracting clientId from token");
            String clientId = extractClaim(token, Claims::getSubject);
            log.debug("Extracted clientId: {}", clientId);
            return clientId;
        } catch (Exception e) {
            log.error("Error extracting clientId from token: {}", e.getMessage());
            throw new RuntimeException("Error extracting clientId from JWT token", e);
        }
    }

    public String extractAbi(String token) {
        try {
            log.debug("Extracting ABI from token");
            String abi = extractAllClaims(token).get("abi", String.class);
            log.debug("Extracted ABI: {}", abi);
            return abi;
        } catch (Exception e) {
            log.error("Error extracting ABI from token: {}", e.getMessage());
            throw new RuntimeException("Error extracting ABI from JWT token", e);
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            log.error("Error extracting claim from token: {}", e.getMessage());
            throw new RuntimeException("Error extracting claim from JWT token", e);
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            log.debug("Extracting all claims from token");
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.debug("Successfully extracted claims from token");
            return claims;
        } catch (Exception e) {
            log.error("Error extracting claims from token: {}", e.getMessage());
            throw new RuntimeException("Error extracting claims from JWT token", e);
        }
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
