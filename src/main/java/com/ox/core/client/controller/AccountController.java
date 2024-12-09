package com.ox.core.client.controller;

import com.ox.core.client.model.dto.AccountsResponse;
import com.ox.core.client.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/banks/{abi}/clients/{clientId}/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account management API")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    @Operation(summary = "Get client accounts", description = "Get all accounts for a client")
    public ResponseEntity<AccountsResponse> getClientAccounts(
            @PathVariable String abi,
            @PathVariable String clientId
    ) {
        return ResponseEntity.ok(accountService.getClientAccounts(abi, clientId));
    }
}
