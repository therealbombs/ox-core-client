package com.ox.core.client.service;

import com.ox.core.client.model.dto.ClientResponse;

public interface ClientService {

    /**
     * Get detailed client information by ABI and client ID.
     *
     * @param abi Bank ABI code
     * @param clientId Client identifier
     * @return Client details response
     */
    ClientResponse getClientDetails(String abi, String clientId);

    /**
     * Check if a client exists by their client ID.
     *
     * @param clientId Client identifier
     * @return true if client exists, false otherwise
     */
    boolean existsByClientId(String clientId);

    /**
     * Validate a client by their client ID.
     * This is used during authentication to verify the client exists and is valid.
     *
     * @param clientId Client identifier
     * @return true if client is valid, false otherwise
     */
    boolean validateClient(String clientId);
}
