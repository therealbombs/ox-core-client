package com.ox.core.client.service;

import com.ox.core.client.model.dto.ClientResponse;
import com.ox.core.client.model.dto.InquiryRequest;
import com.ox.core.client.model.dto.InquiryResponse;

public interface ClientService {
    ClientResponse getClientDetails(String abi, String clientId);
    boolean existsByClientId(String clientId);
    boolean validateClient(String clientId);
    
    /**
     * Inquire about a client using their fiscal code and bank ABI.
     * This method is used to find client information without requiring authentication.
     *
     * @param request The inquiry request containing ABI and fiscal code
     * @return Client inquiry response with basic client information
     */
    InquiryResponse inquireClient(InquiryRequest request);
}
