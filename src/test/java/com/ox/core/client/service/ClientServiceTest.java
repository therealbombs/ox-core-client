package com.ox.core.client.service;

import com.ox.core.client.exception.ResourceNotFoundException;
import com.ox.core.client.model.dto.ClientResponse;
import com.ox.core.client.model.entity.Client;
import com.ox.core.client.repository.ClientRepository;
import com.ox.core.client.service.impl.ClientServiceImpl;
import com.ox.core.client.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client testClient;
    private final String TEST_ABI = "12345";
    private final String TEST_CLIENT_ID = "C001";

    @BeforeEach
    void setUp() {
        testClient = TestDataBuilder.createTestClient(TEST_CLIENT_ID, TEST_ABI);
    }

    @Test
    void getClientDetails_Success() {
        // Arrange
        when(clientRepository.findByAbiAndClientId(TEST_ABI, TEST_CLIENT_ID))
                .thenReturn(Optional.of(testClient));

        // Act
        ClientResponse response = clientService.getClientDetails(TEST_ABI, TEST_CLIENT_ID);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_CLIENT_ID, response.getClientId());
        assertEquals(TEST_ABI, response.getAbi());
        assertEquals(testClient.getName(), response.getPersonalInfo().getName());
        assertEquals(testClient.getSurname(), response.getPersonalInfo().getSurname());
        assertEquals(testClient.getFiscalCode(), response.getPersonalInfo().getFiscalCode());
        assertEquals(testClient.getPreferredLanguage(), response.getPreferences().getLanguage());
        assertEquals(testClient.getLastAccess(), response.getPreferences().getLastAccess());
    }

    @Test
    void getClientDetails_NotFound() {
        // Arrange
        when(clientRepository.findByAbiAndClientId(anyString(), anyString()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> clientService.getClientDetails(TEST_ABI, TEST_CLIENT_ID));
    }
}
