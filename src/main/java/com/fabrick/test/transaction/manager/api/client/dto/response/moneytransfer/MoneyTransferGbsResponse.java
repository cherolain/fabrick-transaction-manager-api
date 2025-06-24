package com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoneyTransferGbsResponse {
    private String moneyTransferId;
    private String status;
    private String direction;
    private CreditorResponse creditor;
    private DebtorResponse debtor;
    private String cro;
    private String uri;
    private String trn;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime createdDatetime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime accountedDatetime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate debtorValueDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creditorValueDate;

    private AmountResponse amount;
    private Boolean isUrgent;
    private Boolean isInstant;
    private String feeType;
    private String feeAccountId;
    private List<FeeResponse> fees;
    private Boolean hasTaxRelief;
}