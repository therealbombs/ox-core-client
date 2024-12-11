package com.ox.core.client.service.impl;

import com.ox.core.client.model.dto.AccountResponse;
import com.ox.core.client.model.dto.AccountsResponse;
import com.ox.core.client.model.entity.Account;
import com.ox.core.client.model.entity.AccountHolder;
import com.ox.core.client.repository.AccountRepository;
import com.ox.core.client.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public AccountsResponse getClientAccounts(String abi, String clientId) {
        List<Account> accounts = accountRepository.findByAbiAndClientId(abi, clientId);

        List<AccountResponse> accountResponses = accounts.stream()
                .map(this::mapToAccountResponse)
                .collect(Collectors.toList());

        return AccountsResponse.builder()
                .accounts(accountResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalBalance(String abi, String clientId) {
        List<Account> accounts = accountRepository.findByAbiAndClientId(abi, clientId);
        return accounts.stream()
                .mapToDouble(Account::getBalance)
                .sum();
    }

    private AccountResponse mapToAccountResponse(Account account) {
        List<AccountResponse.AccountHolderInfo> holders = account.getAccountHolders().stream()
                .map(this::mapToAccountHolderInfo)
                .collect(Collectors.toList());

        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .abi(account.getAbi())
                .accountType(account.getAccountType())
                .accountNumber(account.getAccountNumber())
                .status(account.getStatus())
                .iban(account.getIban())
                .balance(account.getBalance())
                .holders(holders)
                .build();
    }

    private AccountResponse.AccountHolderInfo mapToAccountHolderInfo(AccountHolder holder) {
        return AccountResponse.AccountHolderInfo.builder()
                .clientId(holder.getClient().getClientId())
                .holderType(holder.getHolderType().toString())
                .name(holder.getClient().getName())
                .surname(holder.getClient().getSurname())
                .build();
    }
}
