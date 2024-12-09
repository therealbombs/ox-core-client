package com.ox.core.client.service;

import com.ox.core.client.model.dto.AuthenticationRequest;
import com.ox.core.client.model.dto.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
}
