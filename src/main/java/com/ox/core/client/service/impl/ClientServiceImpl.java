package com.ox.core.client.service.impl;

import com.ox.core.client.exception.ResourceNotFoundException;
import com.ox.core.client.model.dto.ClientResponse;
import com.ox.core.client.model.entity.Client;
import com.ox.core.client.model.dto.InquiryRequest;
import com.ox.core.client.model.dto.InquiryResponse;
import com.ox.core.client.repository.ClientRepository;
import com.ox.core.client.repository.AccountRepository;
import com.ox.core.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

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
                        .lastAccess(client.getPreviousAccess())
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
        return existsByClientId(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByClientId(String clientId) {
        log.debug("Checking if client exists with ID: {}", clientId);
        boolean exists = clientRepository.existsByClientId(clientId);
        log.debug("Client existence check result for ID {}: {}", clientId, exists);
        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public InquiryResponse inquireClient(InquiryRequest request) {
        log.debug("Processing client inquiry for ABI: {}, fiscal code: {}", request.getAbi(), request.getFiscalCode());
        
        Optional<Client> clientOpt = clientRepository.findByAbiAndFiscalCode(request.getAbi(), request.getFiscalCode());
        
        if (clientOpt.isEmpty()) {
            log.debug("No client found for ABI: {} and fiscal code: {}", request.getAbi(), request.getFiscalCode());
            return InquiryResponse.builder()
                    .abi(request.getAbi())
                    .fiscalCode(request.getFiscalCode())
                    .clientExists(false)
                    .numberOfAccounts(0)
                    .build();
        }
        
        Client client = clientOpt.get();
        int accountCount = accountRepository.countAccountsByClientId(client.getClientId());
        
        log.debug("Found client: {} with {} accounts", client.getClientId(), accountCount);
        
        return InquiryResponse.builder()
                .clientId(client.getClientId())
                .abi(client.getAbi())
                .fullName(client.getName() + " " + client.getSurname())
                .fiscalCode(client.getFiscalCode())
                .clientExists(true)
                .numberOfAccounts(accountCount)
                .build();
    }
}
