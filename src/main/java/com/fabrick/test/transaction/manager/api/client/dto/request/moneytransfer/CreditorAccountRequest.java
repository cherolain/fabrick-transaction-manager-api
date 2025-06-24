package com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer;

import com.fabrick.test.transaction.manager.api.validation.ValidBicCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidBicCode // Custom validation to ensure bicCode is required when accountCode is a SWIFT code
public class CreditorAccountRequest {
    @NotBlank(message = "accountCode must not be blank")
    @Pattern(
            regexp = "([A-Z]{2}\\d{2}[A-Z0-9]{1,30})|([A-Z0-9]{8,34})",
            message = "accountCode must be a valid IBAN or SWIFT account number"
    )
    private String accountCode;

    private String bicCode;
}