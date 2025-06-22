package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.MoneyTransferRequest;
import com.fabrick.test.transaction.manager.api.client.dto.request.transactions.TransactionSearchRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.balance.Balance;
import com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer.MoneyTransferResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionListResponse;
import com.fabrick.test.transaction.manager.api.service.handler.BalanceQueryHandler;
import com.fabrick.test.transaction.manager.api.service.handler.MoneyTransferCommandHandler;
import com.fabrick.test.transaction.manager.api.service.handler.TransactionsQueryHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GbsBankingApiFacadeTest {

    @Mock
    private BalanceQueryHandler balanceQueryHandler;
    @Mock
    private TransactionsQueryHandler transactionsQueryHandler;
    @Mock
    private MoneyTransferCommandHandler moneyTransferCommandHandler;

    @InjectMocks
    private GbsBankingApiFacade fabrickApiFacade;

    @Test
    void getAccountBalance_shouldDelegateToBalanceQueryHandler() {
        String accountId = "123";
        var expectedBalance = new Balance();

        when(balanceQueryHandler.handle(accountId)).thenReturn(expectedBalance);

        Balance result = fabrickApiFacade.getBalance(accountId);

        assertNotNull(result);
        assertEquals(expectedBalance, result);

        verify(balanceQueryHandler).handle(accountId);
    }

    @Test
    void getTransactions_shouldBuildQueryAndDelegateToTransactionsQueryHandler() {
        String accountId = "456";
        var searchRequest = new TransactionSearchRequest(LocalDate.now(), LocalDate.now().plusDays(1));
        var expectedResponse = new TransactionListResponse();

        ArgumentCaptor<TransactionsQueryHandler.Query> queryCaptor = ArgumentCaptor.forClass(TransactionsQueryHandler.Query.class);

        when(transactionsQueryHandler.handle(any(TransactionsQueryHandler.Query.class))).thenReturn(expectedResponse);

        TransactionListResponse result = fabrickApiFacade.getTransactions(accountId, searchRequest);

        assertEquals(expectedResponse, result);

        verify(transactionsQueryHandler).handle(queryCaptor.capture());

        TransactionsQueryHandler.Query capturedQuery = queryCaptor.getValue();
        assertEquals(accountId, capturedQuery.accountId());
        assertEquals(searchRequest, capturedQuery.searchRequest());
    }

    @Test
    void transferMoney_shouldBuildCommandAndDelegateToMoneyTransferCommandHandler() {
        String accountId = "789";
        var moneyTransferRequest = new MoneyTransferRequest();
        var expectedResponse = new MoneyTransferResponse();

        ArgumentCaptor<MoneyTransferCommandHandler.Command> commandCaptor = ArgumentCaptor.forClass(MoneyTransferCommandHandler.Command.class);

        when(moneyTransferCommandHandler.handle(any(MoneyTransferCommandHandler.Command.class))).thenReturn(expectedResponse);

        MoneyTransferResponse result = fabrickApiFacade.transferMoney(accountId, moneyTransferRequest);

        assertEquals(expectedResponse, result);

        verify(moneyTransferCommandHandler).handle(commandCaptor.capture());

        MoneyTransferCommandHandler.Command capturedCommand = commandCaptor.getValue();
        assertEquals(accountId, capturedCommand.accountId());
        assertEquals(moneyTransferRequest, capturedCommand.moneyTransferRequest());
        assertNotNull(capturedCommand.timeZone());
    }
}