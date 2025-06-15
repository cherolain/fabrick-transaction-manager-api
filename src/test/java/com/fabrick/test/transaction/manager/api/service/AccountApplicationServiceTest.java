package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.MoneyTransferRequest;
import com.fabrick.test.transaction.manager.api.client.dto.request.transactions.TransactionSearchRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.balance.Balance;
import com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer.MoneyTransferResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionListResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AccountApplicationServiceTest {
    private final BalanceService balanceService = Mockito.mock(BalanceService.class);
    private final MoneyTransferService moneyTransferService = Mockito.mock(MoneyTransferService.class);
    private final TransactionService transactionService = Mockito.mock(TransactionService.class);
    private final AccountApplicationService accountApplicationService = new AccountApplicationService(balanceService, moneyTransferService, transactionService);

    @Test
    public void testGetBalance() {
        Mockito.when(balanceService.getAccountBalance(Mockito.any()))
                .thenReturn(new Balance());

        var res = accountApplicationService.getBalance("123");

        Assertions.assertNotNull(res);
    }

    @Test
    public void testTransferMoney() {
        Mockito.when(moneyTransferService.transferMoney(Mockito.any(), Mockito.any()))
                .thenReturn(new MoneyTransferResponse());

        var res = accountApplicationService.transferMoney("123", new MoneyTransferRequest());

        Assertions.assertNotNull(res);
    }

    @Test
    public void getTransactions() {
        Mockito.when(transactionService.getTransactions(Mockito.any(), Mockito.any()))
                .thenReturn(new TransactionListResponse());

        var res = accountApplicationService.getTransactions("123", new TransactionSearchRequest());

        Assertions.assertNotNull(res);
    }

}