package com.ox.core.client.integration;

import com.ox.core.client.model.dto.ClientResponse;
import com.ox.core.client.model.entity.Client;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ClientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    private final String TEST_ABI = "12345";
    private final String TEST_CLIENT_ID = "C001";

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();
        Client testClient = TestDataBuilder.createTestClient(TEST_CLIENT_ID, TEST_ABI);
        clientRepository.save(testClient);
    }

    @Test
    @WithMockUser
    void getClientDetails_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/banks/{abi}/clients/{clientId}", TEST_ABI, TEST_CLIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains(TEST_CLIENT_ID));
        assertTrue(content.contains(TEST_ABI));
    }

    @Test
    @WithMockUser
    void getClientDetails_NotFound() throws Exception {
        mockMvc.perform(get("/banks/{abi}/clients/{clientId}", TEST_ABI, "NONEXISTENT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getClientDetails_Unauthorized() throws Exception {
        mockMvc.perform(get("/banks/{abi}/clients/{clientId}", TEST_ABI, TEST_CLIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
