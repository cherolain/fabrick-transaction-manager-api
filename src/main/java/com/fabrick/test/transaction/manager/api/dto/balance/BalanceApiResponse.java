package com.fabrick.test.transaction.manager.api.dto.balance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceApiResponse {

    private LocalDate date;

    private BigDecimal balance;

    private BigDecimal availableBalance;

    private String currency;
}