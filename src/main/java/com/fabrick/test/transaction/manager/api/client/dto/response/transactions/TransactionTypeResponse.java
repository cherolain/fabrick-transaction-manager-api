package com.fabrick.test.transaction.manager.api.client.dto.response.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionTypeResponse {
    private String enumeration;
    private String value;
}