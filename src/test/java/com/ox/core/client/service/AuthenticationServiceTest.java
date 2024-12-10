package com.ox.core.client.service;

import com.ox.core.client.model.entity.Client;
import com.ox.core.client.model.dto.AuthenticationRequest;
import com.ox.core.client.model.dto.AuthenticationResponse;
import com.ox.core.client.repository.ClientRepository;
import com.ox.core.client.security.JwtTokenProvider;
import com.ox.core.client.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthenticationServiceTest {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private static final String CLIENT_ID = "C001";
    private static final String ABI = "01234";
    private static final String PASSWORD = "Password123!";

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();
        Client client = Client.builder()
                .clientId(CLIENT_ID)
                .abi(ABI)
                .password(passwordEncoder.encode(PASSWORD))
                .failedAttempts(0)
                .passwordChangeRequired(false)
                .build();
        clientRepository.save(client);
    }

    @Test
    void authenticate_WithValidCredentials_ShouldReturnToken() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest();
        request.setClientId(CLIENT_ID);
        request.setAbi(ABI);
        request.setPassword(PASSWORD);

        // Act
        AuthenticationResponse response = authenticationService.authenticate(request);

        // Assert
        assertNotNull(response.getToken());
        assertEquals(CLIENT_ID, response.getClientId());
        assertEquals(ABI, response.getAbi());
        assertEquals(3, response.getRemainingAttempts());
        assertNull(response.getLockedUntil());
    }

    @Test
    void authenticate_WithInvalidPassword_ShouldDecrementAttempts() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest();
        request.setClientId(CLIENT_ID);
        request.setAbi(ABI);
        request.setPassword("WrongPassword123!");

        // Act
        AuthenticationResponse response = authenticationService.authenticate(request);

        // Assert
        assertNull(response.getToken());
        assertEquals(2, response.getRemainingAttempts());
        assertNull(response.getLockedUntil());
    }

    @Test
    void authenticate_WithTooManyFailedAttempts_ShouldLockAccount() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest();
        request.setClientId(CLIENT_ID);
        request.setAbi(ABI);
        request.setPassword("WrongPassword123!");

        // Act & Assert - First attempt
        AuthenticationResponse response1 = authenticationService.authenticate(request);
        assertEquals(2, response1.getRemainingAttempts());

        // Second attempt
        AuthenticationResponse response2 = authenticationService.authenticate(request);
        assertEquals(1, response2.getRemainingAttempts());

        // Third attempt - should lock
        AuthenticationResponse response3 = authenticationService.authenticate(request);
        assertEquals(0, response3.getRemainingAttempts());
        assertNotNull(response3.getLockedUntil());
        assertTrue(response3.getLockedUntil().isAfter(LocalDateTime.now()));
    }

    @Test
    void unlockAccount_WithExpiredLockAndValidCredentials_ShouldUnlock() {
        // Arrange
        Client client = clientRepository.findByClientIdAndAbi(CLIENT_ID, ABI).get();
        client.setLockedUntil(LocalDateTime.now().minusMinutes(1));
        client.setFailedAttempts(3);
        clientRepository.save(client);

        AuthenticationRequest request = new AuthenticationRequest();
        request.setClientId(CLIENT_ID);
        request.setAbi(ABI);
        request.setPassword(PASSWORD);

        // Act
        AuthenticationResponse response = authenticationService.unlockAccount(request);

        // Assert
        assertNotNull(response.getToken());
        assertEquals(3, response.getRemainingAttempts());
        assertNull(response.getLockedUntil());

        // Verify client state in database
        Client updatedClient = clientRepository.findByClientIdAndAbi(CLIENT_ID, ABI).get();
        assertEquals(0, updatedClient.getFailedAttempts());
        assertNull(updatedClient.getLockedUntil());
    }
}
