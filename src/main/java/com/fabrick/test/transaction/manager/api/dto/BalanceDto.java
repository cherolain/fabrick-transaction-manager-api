package com.fabrick.test.transaction.manager.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) that represents the balance of a bank account.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BalanceDto {

    /**
     * Date of the balance.
     */
    private LocalDate date;

    /**
     * Balance of the account.
     */
    private BigDecimal balance;

    /**
     * Available balance of the account.
     */
    private BigDecimal availableBalance;

    /**
     * Currency of the account balance.
     */
    private String currency;
}