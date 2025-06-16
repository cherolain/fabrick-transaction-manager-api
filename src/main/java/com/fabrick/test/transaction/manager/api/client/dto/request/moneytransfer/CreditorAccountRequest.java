package com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditorAccountRequest {
    @NotBlank(message = "accountCode must not be blank")
    private String accountCode;

    private String bicCode;
}