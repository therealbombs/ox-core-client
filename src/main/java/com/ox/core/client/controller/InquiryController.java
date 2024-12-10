package com.ox.core.client.controller;

import com.ox.core.client.model.dto.InquiryRequest;
import com.ox.core.client.model.dto.InquiryResponse;
import com.ox.core.client.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/clients/inquiries")
@RequiredArgsConstructor
@Tag(name = "Client Inquiries", description = "Client inquiry operations")
public class InquiryController {

    private final ClientService clientService;

    @PostMapping
    @Operation(
        summary = "Inquire client information",
        description = "Inquire about a client using their fiscal code and bank ABI. " +
                     "This endpoint does not require authentication and returns basic client information."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Inquiry successful",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = InquiryResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "clientId": "C001",
                        "abi": "01234",
                        "fullName": "Mario Rossi",
                        "fiscalCode": "RSSMRA80A01H501A",
                        "clientExists": true,
                        "numberOfAccounts": 2
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request format or missing required fields",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = """
                    {
                        "timestamp": "2024-12-10T11:01:26+01:00",
                        "status": 400,
                        "error": "Bad Request",
                        "message": "Invalid request format",
                        "path": "/api/v1/clients/inquiries"
                    }
                    """)
            )
        )
    })
    public ResponseEntity<InquiryResponse> inquireClient(
            @Valid @RequestBody InquiryRequest request
    ) {
        log.info("Received inquiry request for ABI: {}, fiscal code: {}", request.getAbi(), request.getFiscalCode());
        InquiryResponse response = clientService.inquireClient(request);
        log.info("Inquiry response: clientExists={}, numberOfAccounts={}", response.isClientExists(), response.getNumberOfAccounts());
        return ResponseEntity.ok(response);
    }
}
