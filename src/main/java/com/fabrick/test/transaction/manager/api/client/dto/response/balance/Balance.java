package com.fabrick.test.transaction.manager.api.client.dto.response.balance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) that represents the balance of a bank account.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Balance {

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