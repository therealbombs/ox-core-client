package com.ox.core.client.integration;

import com.ox.core.client.model.entity.Account;
import com.ox.core.client.model.entity.Client;
import com.ox.core.client.repository.AccountRepository;
import com.ox.core.client.repository.ClientRepository;
import com.ox.core.client.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    private final String TEST_ABI = "12345";
    private final String TEST_CLIENT_ID = "C001";

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        clientRepository.deleteAll();

        Client testClient = TestDataBuilder.createTestClient(TEST_CLIENT_ID, TEST_ABI);
        clientRepository.save(testClient);

        Set<Account> accounts = TestDataBuilder.createTestAccountsForClient(TEST_ABI, testClient);
        accounts.forEach(accountRepository::save);
    }

    @Test
    @WithMockUser
    void getClientAccounts_Success() throws Exception {
        mockMvc.perform(get("/banks/{abi}/clients/{clientId}/accounts", TEST_ABI, TEST_CLIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts").exists())
                .andExpect(jsonPath("$.accounts", hasSize(3)))
                .andExpect(jsonPath("$.accounts[0].accountId").exists())
                .andExpect(jsonPath("$.accounts[0].accountType").exists())
                .andExpect(jsonPath("$.accounts[0].status").exists())
                .andExpect(jsonPath("$.accounts[0].iban").exists())
                .andExpect(jsonPath("$.accounts[0].holders").exists());
    }

    @Test
    @WithMockUser
    void getClientAccounts_NoAccounts() throws Exception {
        mockMvc.perform(get("/banks/{abi}/clients/{clientId}/accounts", TEST_ABI, "NONEXISTENT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts").exists())
                .andExpect(jsonPath("$.accounts", hasSize(0)));
    }

    @Test
    void getClientAccounts_Unauthorized() throws Exception {
        mockMvc.perform(get("/banks/{abi}/clients/{clientId}/accounts", TEST_ABI, TEST_CLIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
