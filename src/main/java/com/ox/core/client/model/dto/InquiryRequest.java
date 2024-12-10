package com.ox.core.client.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for client inquiry")
public class InquiryRequest {
    
    @NotBlank(message = "ABI is required")
    @Pattern(regexp = "^\\d{5}$", message = "ABI must be exactly 5 digits")
    @Schema(description = "Bank ABI code", example = "01234")
    private String abi;

    @NotBlank(message = "Fiscal code is required")
    @Pattern(regexp = "^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$", 
            message = "Invalid fiscal code format")
    @Schema(description = "Client's fiscal code", example = "RSSMRA80A01H501A")
    private String fiscalCode;
}
