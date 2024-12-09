package com.ox.core.client.service;

import com.ox.core.client.model.dto.AccountResponse;
import com.ox.core.client.model.dto.AccountsResponse;
import com.ox.core.client.model.entity.Account;
import com.ox.core.client.model.entity.Client;
import com.ox.core.client.repository.AccountRepository;
import com.ox.core.client.service.impl.AccountServiceImpl;
import com.ox.core.client.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Client testClient;
    private List<Account> testAccounts;
    private final String TEST_ABI = "12345";
    private final String TEST_CLIENT_ID = "C001";

    @BeforeEach
    void setUp() {
        testClient = TestDataBuilder.createTestClient(TEST_CLIENT_ID, TEST_ABI);
        Set<Account> accountSet = TestDataBuilder.createTestAccountsForClient(TEST_ABI, testClient);
        testAccounts = new ArrayList<>(accountSet);
    }

    @Test
    void getClientAccounts_Success() {
        // Arrange
        when(accountRepository.findByAbiAndClientId(TEST_ABI, TEST_CLIENT_ID))
                .thenReturn(testAccounts);

        // Act
        AccountsResponse response = accountService.getClientAccounts(TEST_ABI, TEST_CLIENT_ID);

        // Assert
        assertNotNull(response);
        assertFalse(response.getAccounts().isEmpty());
        assertEquals(testAccounts.size(), response.getAccounts().size());

        AccountResponse firstAccount = response.getAccounts().get(0);
        assertNotNull(firstAccount.getAccountId());
        assertNotNull(firstAccount.getAccountType());
        assertNotNull(firstAccount.getStatus());
        assertNotNull(firstAccount.getIban());
        assertFalse(firstAccount.getHolders().isEmpty());

        AccountResponse.AccountHolderInfo firstHolder = firstAccount.getHolders().get(0);
        assertEquals(TEST_CLIENT_ID, firstHolder.getClientId());
        assertEquals(testClient.getName(), firstHolder.getName());
        assertEquals(testClient.getSurname(), firstHolder.getSurname());
    }

    @Test
    void getClientAccounts_EmptyList() {
        // Arrange
        when(accountRepository.findByAbiAndClientId(anyString(), anyString()))
                .thenReturn(new ArrayList<>());

        // Act
        AccountsResponse response = accountService.getClientAccounts(TEST_ABI, TEST_CLIENT_ID);

        // Assert
        assertNotNull(response);
        assertTrue(response.getAccounts().isEmpty());
    }
}
