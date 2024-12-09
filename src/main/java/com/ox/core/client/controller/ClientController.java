package com.ox.core.client.controller;

import com.ox.core.client.model.dto.ClientResponse;
import com.ox.core.client.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/banks/{abi}/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Client management API")
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/{clientId}")
    @Operation(summary = "Get client details", description = "Get client details by ABI and client ID")
    public ResponseEntity<ClientResponse> getClientDetails(
            @PathVariable String abi,
            @PathVariable String clientId
    ) {
        return ResponseEntity.ok(clientService.getClientDetails(abi, clientId));
    }
}
