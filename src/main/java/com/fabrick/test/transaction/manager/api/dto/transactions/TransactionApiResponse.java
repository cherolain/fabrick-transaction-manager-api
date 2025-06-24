package com.fabrick.test.transaction.manager.api.dto.transactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionApiResponse {
    private LocalDate accountingDate;
    private LocalDate valueDate;
    private TransactionTypeApiResponse type;
    private BigDecimal amount;
    private String currency;
    private String description;
}