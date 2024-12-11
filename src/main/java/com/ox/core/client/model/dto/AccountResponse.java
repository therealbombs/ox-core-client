package com.ox.core.client.model.dto;

import com.ox.core.client.model.enums.AccountStatus;
import com.ox.core.client.model.enums.AccountType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AccountResponse {
    private String accountId;
    private String abi;
    private AccountType accountType;
    private String accountNumber;
    private AccountStatus status;
    private String iban;
    private Double balance;
    private List<AccountHolderInfo> holders;

    @Data
    @Builder
    public static class AccountHolderInfo {
        private String clientId;
        private String holderType;
        private String name;
        private String surname;
    }
}
