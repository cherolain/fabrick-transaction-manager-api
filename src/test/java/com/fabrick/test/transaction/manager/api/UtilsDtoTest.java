package com.fabrick.test.transaction.manager.api;

import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.CreditorAccountRequest;
import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.CreditorRequest;
import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.MoneyTransferRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public final class UtilsDtoTest {

    private UtilsDtoTest() {
    }

    public static MoneyTransferResponse buildMockMoneyTransferResponse() {
        CreditorResponse creditor = CreditorResponse.builder()
                .name("John Doe")
                .account(CreditorAccountResponse.builder()
                        .accountCode("IT23A0336844430152923804660")
                        .bicCode("SELBIT2BXXX")
                        .build())
                .address(CreditorAddressResponse.builder()
                        .address(null) // L'esempio JSON aveva null, manteniamo coerenza
                        .city(null)
                        .countryCode(null)
                        .build())
                .build();

        DebtorResponse debtor = DebtorResponse.builder()
                .name("")
                .account(DebtorAccountResponse.builder()
                        .accountCode("IT61F0326802230280596327270")
                        .bicCode(null)
                        .build())
                .build();

        AmountResponse amount = AmountResponse.builder()
                .debtorAmount(new BigDecimal("800"))
                .debtorCurrency("EUR")
                .creditorAmount(new BigDecimal("800"))
                .creditorCurrency("EUR")
                .creditorCurrencyDate(LocalDate.parse("2019-04-10"))
                .exchangeRate(new BigDecimal("1"))
                .build();

        FeeResponse fee1 = FeeResponse.builder()
                .feeCode("MK001")
                .description("Money transfer execution fee")
                .amount(new BigDecimal("0.25"))
                .currency("EUR")
                .build();

        FeeResponse fee2 = FeeResponse.builder()
                .feeCode("MK003")
                .description("Currency exchange fee")
                .amount(new BigDecimal("3.5"))
                .currency("EUR")
                .build();

        return MoneyTransferResponse.builder()
                .moneyTransferId("452516859427")
                .status("EXECUTED")
                .direction("OUTGOING")
                .creditor(creditor)
                .debtor(debtor)
                .cro("1234566788907")
                .uri("REMITTANCE_INFORMATION")
                .trn("AJFSAD1234566788907CCSFDGTGVGV")
                .description("Description")
                .createdDatetime(OffsetDateTime.parse("2019-04-10T10:38:55.949+02:00"))
                .accountedDatetime(OffsetDateTime.parse("2019-04-10T10:38:56.000+02:00"))
                .debtorValueDate(LocalDate.parse("2019-04-10"))
                .creditorValueDate(LocalDate.parse("2019-04-10"))
                .amount(amount)
                .isUrgent(false)
                .isInstant(false)
                .feeType("SHA")
                .feeAccountId("12345678")
                .fees(List.of(fee1, fee2))
                .hasTaxRelief(true)
                .build();
    }

    public static MoneyTransferRequest buildMockMoneyTransferRequest() {
        CreditorRequest creditor = new CreditorRequest();
        creditor.setName("Mario Rossi");
        CreditorAccountRequest account = new CreditorAccountRequest();
        account.setAccountCode("IT60X0542811101000000123457");
        account.setBicCode("GEBAMMM"); // Esempio BIC
        creditor.setAccount(account);
        // L'indirizzo è opzionale nella richiesta (a meno di specifici scenari internazionali)
        // creditor.setAddress(new CreditorAddressRequest("Via Verdi 1", "Milano", "IT"));

        MoneyTransferRequest request = new MoneyTransferRequest();
        request.setCreditor(creditor);
        request.setDescription("Pagamento fattura #123");
        request.setAmount(new BigDecimal("500.00"));
        request.setCurrency("EUR");
        request.setExecutionDate(LocalDate.now().plusDays(5)); // Data futura
        request.setIsUrgent(false);
        request.setIsInstant(false);
        request.setFeeType("SHA");
        // TaxRelief è opzionale
        return request;
    }
}