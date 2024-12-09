package com.ox.core.client.service;

import com.ox.core.client.model.dto.ClientResponse;

public interface ClientService {
    ClientResponse getClientDetails(String abi, String clientId);
}
