package com.fabrick.test.transaction.manager.api.client.dto.request.transactions;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSearchRequest {

    @NotNull(message = "fromAccountingDate must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromAccountingDate;

    @NotNull(message = "toAccountingDate must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toAccountingDate;
}