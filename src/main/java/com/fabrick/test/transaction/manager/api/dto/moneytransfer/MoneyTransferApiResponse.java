package com.fabrick.test.transaction.manager.api.dto.moneytransfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Public Data Transfer Object representing a completed money transfer.
 * This class defines the stable and clean contract exposed by our API to clients,
 * abstracting the details of the internal GBS API response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoneyTransferApiResponse {

    private String moneyTransferId;
    private String status;
    private String cro;
    private String trn;

    private CreditorApiResponse creditor;

    private String description;
    private AmountApiResponse amount;

    private String feeType;
    private List<FeeApiResponse> fees;

    private Boolean isUrgent;
    private Boolean isInstant;
    private OffsetDateTime createdDatetime;
    private OffsetDateTime accountedDatetime;
}