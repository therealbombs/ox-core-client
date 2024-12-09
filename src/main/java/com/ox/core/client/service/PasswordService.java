package com.ox.core.client.service;

import com.ox.core.client.model.dto.ChangePasswordRequest;
import com.ox.core.client.model.dto.ChangePasswordResponse;

public interface PasswordService {
    ChangePasswordResponse changePassword(String clientId, ChangePasswordRequest request);
}
