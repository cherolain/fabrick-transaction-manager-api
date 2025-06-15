package com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtorResponse {
    private String name;           // Nome del debitore
    private DebtorAccountResponse account; // Dettagli del conto del debitore
}