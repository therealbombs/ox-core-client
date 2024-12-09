package com.ox.core.client.util;

import com.ox.core.client.model.entity.Account;
import com.ox.core.client.model.entity.AccountHolder;
import com.ox.core.client.model.entity.Client;
import com.ox.core.client.model.enums.AccountStatus;
import com.ox.core.client.model.enums.AccountType;
import com.ox.core.client.model.enums.HolderType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class TestDataBuilder {

    public static Client createTestClient(String clientId, String abi) {
        return Client.builder()
                .clientId(clientId)
                .abi(abi)
                .fiscalCode("FISCAL" + clientId)
                .name("Test")
                .surname("User" + clientId)
                .preferredLanguage("en")
                .lastAccess(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    public static Account createTestAccount(String accountId, String abi, AccountType type, AccountStatus status) {
        return Account.builder()
                .accountId(accountId)
                .abi(abi)
                .accountType(type)
                .accountNumber("ACC" + accountId)
                .status(status)
                .iban("IT" + abi + "ACC" + accountId)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    public static AccountHolder createTestAccountHolder(Account account, Client client, HolderType type) {
        return AccountHolder.builder()
                .holderId(account.getAccountId() + "_" + client.getClientId())
                .account(account)
                .client(client)
                .holderType(type)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Set<Account> createTestAccountsForClient(String abi, Client client) {
        Set<Account> accounts = new HashSet<>();
        
        // Create different types of accounts
        Account currentAccount = createTestAccount("CA001", abi, AccountType.CURRENT_ACCOUNT, AccountStatus.ACTIVE);
        Account depositAccount = createTestAccount("DA001", abi, AccountType.DEPOSIT_ACCOUNT, AccountStatus.ACTIVE);
        Account securitiesAccount = createTestAccount("SA001", abi, AccountType.SECURITIES_ACCOUNT, AccountStatus.TO_BE_ACTIVATED);

        // Create account holders
        AccountHolder currentHolder = createTestAccountHolder(currentAccount, client, HolderType.PRIMARY);
        AccountHolder depositHolder = createTestAccountHolder(depositAccount, client, HolderType.PRIMARY);
        AccountHolder securitiesHolder = createTestAccountHolder(securitiesAccount, client, HolderType.SECONDARY);

        // Set up bidirectional relationships
        Set<AccountHolder> currentHolders = new HashSet<>();
        currentHolders.add(currentHolder);
        currentAccount.setAccountHolders(currentHolders);

        Set<AccountHolder> depositHolders = new HashSet<>();
        depositHolders.add(depositHolder);
        depositAccount.setAccountHolders(depositHolders);

        Set<AccountHolder> securitiesHolders = new HashSet<>();
        securitiesHolders.add(securitiesHolder);
        securitiesAccount.setAccountHolders(securitiesHolders);

        accounts.add(currentAccount);
        accounts.add(depositAccount);
        accounts.add(securitiesAccount);

        return accounts;
    }
}
