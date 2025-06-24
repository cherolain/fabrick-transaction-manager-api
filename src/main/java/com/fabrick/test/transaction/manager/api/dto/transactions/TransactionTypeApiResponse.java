package com.fabrick.test.transaction.manager.api.dto.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionTypeApiResponse {
    private String enumeration;
    private String value;
}