package com.ox.core.client.service;

import com.ox.core.client.model.dto.AuthenticationRequest;
import com.ox.core.client.model.dto.AuthenticationResponse;

public interface AuthenticationService {
    /**
     * Authenticates a client with the provided credentials.
     *
     * @param request Authentication request containing client credentials
     * @return Authentication response with token if successful
     */
    AuthenticationResponse authenticate(AuthenticationRequest request);

    /**
     * Attempts to unlock a locked client account.
     * The account will only be unlocked if:
     * 1. The lock duration has expired
     * 2. The provided credentials are correct
     *
     * @param request Authentication request containing client credentials
     * @return Authentication response with token if unlocking is successful
     */
    AuthenticationResponse unlockAccount(AuthenticationRequest request);
}
