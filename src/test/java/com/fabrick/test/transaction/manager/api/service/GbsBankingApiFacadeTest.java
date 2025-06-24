package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.MoneyTransferRequest;
import com.fabrick.test.transaction.manager.api.client.dto.request.transactions.TransactionSearchRequest;
import com.fabrick.test.transaction.manager.api.dto.balance.BalanceApiResponse;
import com.fabrick.test.transaction.manager.api.dto.moneytransfer.MoneyTransferApiResponse;
import com.fabrick.test.transaction.manager.api.dto.transactions.TransactionApiResponse;
import com.fabrick.test.transaction.manager.api.dto.transactions.TransactionListApiResponse;
import com.fabrick.test.transaction.manager.api.service.handler.BalanceQueryHandler;
import com.fabrick.test.transaction.manager.api.service.handler.MoneyTransferCommandHandler;
import com.fabrick.test.transaction.manager.api.service.handler.TransactionsQueryHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the GbsBankingApiFacade, verifying its orchestration logic.
 */
@ExtendWith(MockitoExtension.class)
class GbsBankingApiFacadeTest {

    @Mock
    private BalanceQueryHandler balanceQueryHandler;
    @Mock
    private TransactionsQueryHandler transactionsQueryHandler;
    @Mock
    private MoneyTransferCommandHandler moneyTransferCommandHandler;

    @InjectMocks
    private GbsBankingApiFacade gbsBankingApiFacade;

    @Test
    void getBalance_shouldDelegateToBalanceQueryHandler() {
        // --- ARRANGE ---
        String accountId = "123";
        var expectedResponse = BalanceApiResponse.builder()
                .date(LocalDate.now())
                .balance(new BigDecimal(100))
                .availableBalance(new BigDecimal(90))
                .currency("EUR")
                .build();

        when(balanceQueryHandler.handle(accountId)).thenReturn(expectedResponse);

        // --- ACT ---
        BalanceApiResponse result = gbsBankingApiFacade.getBalance(accountId);

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(balanceQueryHandler).handle(accountId);
    }

    @Test
    void getTransactions_shouldBuildQueryAndDelegateToTransactionsQueryHandler() {
        String accountId = "456";
        var searchRequest = new TransactionSearchRequest(LocalDate.now(), LocalDate.now().plusDays(1));
        var transaction = TransactionApiResponse.builder()
                .amount(new BigDecimal("100.00"))
                .currency("EUR")
                .description("Test Transaction")
                .accountingDate(LocalDate.now())
                .valueDate(LocalDate.now())
                .build();
        var expected = TransactionListApiResponse.builder()
                .list(Collections.singletonList(transaction))
                .build();

        when(transactionsQueryHandler.handle(any(TransactionsQueryHandler.Query.class))).thenReturn(expected);

        var result = gbsBankingApiFacade.getTransactions(accountId, searchRequest);

        assertNotNull(result);
        assertEquals(expected, result);

        ArgumentCaptor<TransactionsQueryHandler.Query> captor = ArgumentCaptor.forClass(TransactionsQueryHandler.Query.class);
        verify(transactionsQueryHandler).handle(captor.capture());
        assertEquals(accountId, captor.getValue().accountId());
        assertEquals(searchRequest, captor.getValue().searchRequest());
    }

    @Test
    void transferMoney_shouldBuildCommandAndReturnMappedResponse() {
        // --- ARRANGE ---
        String accountId = "789";
        var request = new MoneyTransferRequest();
        var expected = MoneyTransferApiResponse.builder()
                .moneyTransferId("TX-1")
                .status("EXECUTED")
                .build();

        when(moneyTransferCommandHandler.handle(any(MoneyTransferCommandHandler.Command.class))).thenReturn(expected);

        var result = gbsBankingApiFacade.transferMoney(accountId, request);

        assertNotNull(result);
        assertEquals(expected, result);

        ArgumentCaptor<MoneyTransferCommandHandler.Command> captor = ArgumentCaptor.forClass(MoneyTransferCommandHandler.Command.class);
        verify(moneyTransferCommandHandler).handle(captor.capture());
        assertEquals(accountId, captor.getValue().accountId());
        assertEquals(request, captor.getValue().moneyTransferRequest());
        assertNotNull(captor.getValue().timeZone());
    }
}