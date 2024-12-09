package com.ox.core.client.controller;

import com.ox.core.client.model.dto.ChangePasswordRequest;
import com.ox.core.client.model.dto.ChangePasswordResponse;
import com.ox.core.client.service.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/password")
@RequiredArgsConstructor
@Tag(name = "Password", description = "Password management API")
public class PasswordController {

    private final PasswordService passwordService;

    @PostMapping("/change")
    @Operation(summary = "Change password", description = "Change client password. Requires authentication.")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {
        String clientId = authentication.getName();
        return ResponseEntity.ok(passwordService.changePassword(clientId, request));
    }
}
