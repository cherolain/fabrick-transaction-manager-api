package com.fabrick.test.transaction.manager.api.dto.response.moneytransfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditorAccountResponse {
    private String accountCode;
    private String bicCode;
}