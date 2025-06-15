package com.fabrick.test.transaction.manager.api.dto.request.moneytransfer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoneyTransferRequest {

    @NotNull
    @Valid
    private CreditorRequest creditor;

    @FutureOrPresent(message = "executionDate must be today or in the future")
    private LocalDate executionDate;

    private String uri;

    @NotBlank
    @Size(max = 140)
    private String description;

    @NotNull
    @DecimalMin(value = "0.01", message = "amount must be positive")
    private BigDecimal amount;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO code")
    private String currency;

    private Boolean isUrgent = Boolean.FALSE;

    private Boolean isInstant = Boolean.FALSE;

    @Pattern(regexp = "SHA|OUR|BEN", message = "feeType must be one of SHA, OUR, BEN")
    private String feeType = "SHA";

    private String feeAccountId;

    @Valid // Abilita la validazione annidata per TaxReliefRequestDto
    private TaxReliefRequest taxRelief;
}