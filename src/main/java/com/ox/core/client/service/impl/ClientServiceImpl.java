package com.ox.core.client.service.impl;

import com.ox.core.client.exception.ResourceNotFoundException;
import com.ox.core.client.model.dto.ClientResponse;
import com.ox.core.client.model.entity.Client;
import com.ox.core.client.repository.ClientRepository;
import com.ox.core.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getClientDetails(String abi, String clientId) {
        Client client = clientRepository.findByAbiAndClientId(abi, clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        return ClientResponse.builder()
                .clientId(client.getClientId())
                .abi(client.getAbi())
                .personalInfo(ClientResponse.PersonalInfo.builder()
                        .name(client.getName())
                        .surname(client.getSurname())
                        .fiscalCode(client.getFiscalCode())
                        .build())
                .preferences(ClientResponse.Preferences.builder()
                        .language(client.getPreferredLanguage())
                        .lastAccess(client.getLastAccess())
                        .build())
                .auditInfo(ClientResponse.AuditInfo.builder()
                        .createdAt(client.getCreatedAt())
                        .modifiedAt(client.getModifiedAt())
                        .build())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateClient(String clientId) {
        log.debug("Validating client with ID: {}", clientId);
        boolean exists = clientRepository.existsByClientId(clientId);
        log.debug("Client validation result for ID {}: {}", clientId, exists);
        return exists;
    }
}
