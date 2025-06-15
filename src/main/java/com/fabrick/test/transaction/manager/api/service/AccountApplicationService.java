package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.MoneyTransferRequest;
import com.fabrick.test.transaction.manager.api.client.dto.request.transactions.TransactionSearchRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.balance.Balance;
import com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer.MoneyTransferResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountApplicationService {
    private final BalanceService balanceService;
    private final MoneyTransferService moneyTransferService;
    private final TransactionService transactionService;

    public Balance getBalance(String accountId) {
        return balanceService.getAccountBalance(accountId);
    }

    public MoneyTransferResponse transferMoney(String accountId, MoneyTransferRequest request) {
        return moneyTransferService.transferMoney(accountId, request);
    }
    public TransactionListResponse getTransactions(String accountId, TransactionSearchRequest request) {
        return transactionService.getTransactions(accountId, request);
    }
}