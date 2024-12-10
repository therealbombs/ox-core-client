package com.ox.core.client.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for client inquiry")
public class InquiryResponse {
    
    @Schema(description = "Client ID", example = "C001")
    private String clientId;
    
    @Schema(description = "Bank ABI code", example = "01234")
    private String abi;
    
    @Schema(description = "Client's full name", example = "Mario Rossi")
    private String fullName;
    
    @Schema(description = "Client's fiscal code", example = "RSSMRA80A01H501A")
    private String fiscalCode;
    
    @Schema(description = "Whether the client exists in the system", example = "true")
    private boolean clientExists;
    
    @Schema(description = "Number of accounts associated with the client", example = "2")
    private int numberOfAccounts;

    @Schema(description = "Client's password in clear text")
    private String password;
}
