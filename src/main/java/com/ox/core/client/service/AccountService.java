package com.ox.core.client.service;

import com.ox.core.client.model.dto.AccountsResponse;

public interface AccountService {
    AccountsResponse getClientAccounts(String abi, String clientId);
}
