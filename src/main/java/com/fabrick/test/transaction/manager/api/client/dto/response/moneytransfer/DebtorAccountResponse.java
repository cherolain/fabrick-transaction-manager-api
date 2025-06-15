package com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtorAccountResponse {
    private String accountCode; // IBAN o SWIFT del tuo conto
    private String bicCode;     // BIC code della tua banca
}