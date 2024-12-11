package com.ox.core.client.controller;

import com.ox.core.client.model.dto.AccountsResponse;
import com.ox.core.client.security.JwtTokenProvider;
import com.ox.core.client.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account management API")
public class AccountController {

    private final AccountService accountService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/by-bank/{abi}/client/{clientId}")
    @Operation(summary = "Get client accounts", description = "Get all accounts for a client")
    public ResponseEntity<AccountsResponse> getClientAccounts(
            @PathVariable String abi,
            @PathVariable String clientId
    ) {
        return ResponseEntity.ok(accountService.getClientAccounts(abi, clientId));
    }

    @GetMapping("/total-balance")
    @Operation(summary = "Get total balance", description = "Get total balance across all accounts for authenticated client")
    public ResponseEntity<Double> getTotalBalance(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String clientId = jwtTokenProvider.extractClientId(token);
        String abi = jwtTokenProvider.extractAbi(token);
        return ResponseEntity.ok(accountService.getTotalBalance(abi, clientId));
    }

    @GetMapping
    @Operation(summary = "Get accounts", description = "Get all accounts for authenticated client")
    public ResponseEntity<AccountsResponse> getAccounts(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String clientId = jwtTokenProvider.extractClientId(token);
        String abi = jwtTokenProvider.extractAbi(token);
        return ResponseEntity.ok(accountService.getClientAccounts(abi, clientId));
    }
}
