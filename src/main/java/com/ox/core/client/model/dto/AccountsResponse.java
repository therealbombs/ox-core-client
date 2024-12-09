package com.ox.core.client.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AccountsResponse {
    private List<AccountResponse> accounts;
}
