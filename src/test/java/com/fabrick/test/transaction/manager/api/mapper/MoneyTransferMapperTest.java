package com.fabrick.test.transaction.manager.api.mapper;

import com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer.*;
import com.fabrick.test.transaction.manager.api.dto.moneytransfer.FeeApiResponse;
import com.fabrick.test.transaction.manager.api.dto.moneytransfer.MoneyTransferApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class MoneyTransferMapperTest {

    private final MoneyTransferMapper mapper = Mappers.getMapper(MoneyTransferMapper.class);

    private MoneyTransferGbsResponse gbsSource;
    private CreditorResponse creditorSource;
    private CreditorAccountResponse creditorAccountSource;
    private AmountResponse amountSource;
    private FeeResponse feeSource;

    @BeforeEach
    void setUp() {
        creditorAccountSource = CreditorAccountResponse.builder()
                .accountCode("IT60X0542811101000000123456")
                .bicCode("BIC12345")
                .build();
        creditorSource = CreditorResponse.builder()
                .name("John Doe")
                .account(creditorAccountSource)
                .build();

        amountSource = AmountResponse.builder()
                .creditorAmount(new BigDecimal("100.50"))
                .creditorCurrency("EUR")
                .creditorCurrencyDate(LocalDate.of(2024, 5, 21))
                .build();
        feeSource = FeeResponse.builder()
                .feeCode("SHA")
                .amount(new BigDecimal("1.50"))
                .currency("EUR")
                .build();

        gbsSource = MoneyTransferGbsResponse.builder()
                .direction("OUTBOUND")
                .amount(amountSource)
                .moneyTransferId("MT12345")
                .status("EXECUTED")
                .cro("CRO123")
                .trn("TRN456")
                .creditor(creditorSource)
                .description("Payment for invoice #123")
                .fees(List.of(feeSource))
                .isUrgent(false)
                .isInstant(true)
                .feeType("SHA")
                .hasTaxRelief(false)
                .createdDatetime(OffsetDateTime.of(2025, 5, 20, 10, 0, 0, 0, ZoneOffset.UTC))
                .accountedDatetime(OffsetDateTime.of(2025, 5, 21, 10, 5, 0, 0, ZoneOffset.UTC))
                .build();
    }

    @Test
    void shouldCorrectlyMapFullMoneyTransfer() {
        MoneyTransferApiResponse result = mapper.toMoneyTransferApiResponse(gbsSource);

        assertNotNull(result);
        assertEquals(gbsSource.getMoneyTransferId(), result.getMoneyTransferId());
        assertEquals(gbsSource.getStatus(), result.getStatus());

        assertEquals(gbsSource.getCro(), result.getCro());
        assertEquals(gbsSource.getTrn(), result.getTrn());
        assertEquals(gbsSource.getDescription(), result.getDescription());
        assertEquals(gbsSource.getAmount().getCreditorCurrency(), result.getAmount().getCreditorCurrency());
        assertEquals(gbsSource.getFeeType(), result.getFeeType());
        assertEquals(gbsSource.getIsInstant(), result.getIsInstant());
        assertEquals(gbsSource.getIsUrgent(), result.getIsUrgent());
        assertEquals(gbsSource.getCreatedDatetime(), result.getCreatedDatetime());
        assertEquals(gbsSource.getAccountedDatetime(), result.getAccountedDatetime());

        assertNotNull(result.getCreditor());
        assertEquals(gbsSource.getCreditor().getName(), result.getCreditor().getName());
        assertNotNull(result.getCreditor().getAccount());
        assertEquals(gbsSource.getCreditor().getAccount().getAccountCode(), result.getCreditor().getAccount().getAccountCode());

        assertNotNull(result.getAmount());
        assertEquals(gbsSource.getAmount().getCreditorAmount(), result.getAmount().getCreditorAmount());

        assertNotNull(result.getFees());
        assertEquals(gbsSource.getFees().size(), result.getFees().size());
        FeeApiResponse resultFee = result.getFees().getFirst();
        assertEquals(gbsSource.getFees().getFirst().getFeeCode(), resultFee.getFeeCode());
        assertEquals(gbsSource.getFees().getFirst().getCurrency(), resultFee.getCurrency());
    }

    @Test
    void shouldMapCorrectlyWhenOptionalFieldsAreNull() {
        gbsSource.setCro(null);
        gbsSource.setTrn(null);
        gbsSource.setFees(null);

        // Act
        MoneyTransferApiResponse result = mapper.toMoneyTransferApiResponse(gbsSource);

        // Assert
        assertNotNull(result);
        assertNull(result.getCro(), "Il CRO nullo dovrebbe rimanere nullo.");
        assertNull(result.getTrn(), "Il TRN nullo dovrebbe rimanere nullo.");
        assertNull(result.getFees(), "La lista di commissioni nulla dovrebbe rimanere nulla."); // MapStruct di default mappa null a null per le collezioni

        assertEquals("MT12345", result.getMoneyTransferId());
        assertNotNull(result.getCreditor());
    }

    @Test
    void shouldReturnNullWhenSourceIsNull() {
        // Act
        MoneyTransferApiResponse result = mapper.toMoneyTransferApiResponse(null);

        // Assert
        assertNull(result);
    }
}