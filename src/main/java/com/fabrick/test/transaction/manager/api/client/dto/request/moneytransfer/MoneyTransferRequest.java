package com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer;

import com.fabrick.test.transaction.manager.api.validation.ValidExecutionDate;
import com.fabrick.test.transaction.manager.api.validation.ValidFeeType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidExecutionDate // cross field intern validation
@ValidFeeType
public class MoneyTransferRequest {

    @NotNull(message = "creditor must not be null")
    @Valid
    private CreditorRequest creditor;

    @FutureOrPresent(message = "executionDate must be today or in the future")
    private LocalDate executionDate;

    private String uri;

    @NotBlank(message = "description must not be blank")
    @Size(max = 140)
    private String description;

    @NotNull(message = "amount must not be null")
    @DecimalMin(value = "0.01", message = "amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "currency must not be blank")
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