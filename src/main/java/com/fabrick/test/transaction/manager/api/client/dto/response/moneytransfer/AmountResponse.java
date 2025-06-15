package com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmountResponse {
    private BigDecimal debtorAmount;
    private String debtorCurrency;
    private BigDecimal creditorAmount;
    private String creditorCurrency;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creditorCurrencyDate;
    private BigDecimal exchangeRate;
}