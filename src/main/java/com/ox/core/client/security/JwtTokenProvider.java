package com.ox.core.client.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Base64;

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
            byte[] apiKeySecretBytes = Base64.getDecoder().decode(secretKey);
            return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
        } catch (Exception e) {
            log.error("Error generating signing key: {}", e.getMessage());
            throw e;
        }
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        try {
            log.debug("Generating token for user: {}", userDetails.getUsername());
            String token = Jwts.builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(SignatureAlgorithm.HS256, secretKey)
                    .compact();
            log.debug("Successfully generated token for user: {}", userDetails.getUsername());
            return token;
        } catch (Exception e) {
            log.error("Error generating token: {}", e.getMessage());
            throw e;
        }
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String extractUsername(String token) {
        try {
            log.debug("Extracting username from token");
            String username = extractClaim(token, Claims::getSubject);
            log.debug("Extracted username: {}", username);
            return username;
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            throw e;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            log.error("Error extracting claim from token: {}", e.getMessage());
            throw e;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            log.debug("Extracting all claims from token");
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
            log.debug("Successfully extracted claims from token");
            return claims;
        } catch (Exception e) {
            log.error("Error extracting claims from token: {}", e.getMessage());
            throw e;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            log.debug("Validating token for user: {}", userDetails.getUsername());
            final String username = extractUsername(token);
            boolean isValid = (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
            log.debug("Token validation result for user {}: {}", userDetails.getUsername(), isValid);
            return isValid;
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
