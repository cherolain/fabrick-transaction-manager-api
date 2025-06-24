package com.fabrick.test.transaction.manager.api.dto.moneytransfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeApiResponse {
    private String feeCode;
    private String description;
    private BigDecimal amount;
    private String currency;
}