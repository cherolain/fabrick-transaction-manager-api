package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.dto.request.MoneyTransferRequest;
import com.fabrick.test.transaction.manager.api.dto.response.balance.Balance;
import com.fabrick.test.transaction.manager.api.dto.response.moneytransfer.MoneyTransferResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountApplicationService {
    private final BalanceService balanceService;
    private final MoneyTransferService moneyTransferService;

    public Balance getBalance(String accountId) {
        return balanceService.getAccountBalance(accountId);
    }

    public MoneyTransferResponse transferMoney(String accountId, MoneyTransferRequest request) {
        return moneyTransferService.transferMoney(accountId, request);
    }
}