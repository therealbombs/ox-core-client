package com.ox.core.client.service.impl;

import com.ox.core.client.exception.ResourceNotFoundException;
import com.ox.core.client.model.dto.ClientResponse;
import com.ox.core.client.model.entity.Client;
import com.ox.core.client.repository.ClientRepository;
import com.ox.core.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .build();
    }
}
