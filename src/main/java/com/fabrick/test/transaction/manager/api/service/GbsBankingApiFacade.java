package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.MoneyTransferRequest;
import com.fabrick.test.transaction.manager.api.client.dto.request.transactions.TransactionSearchRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.balance.Balance;
import com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer.MoneyTransferResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionListResponse;
import com.fabrick.test.transaction.manager.api.service.handler.BalanceQueryHandler;
import com.fabrick.test.transaction.manager.api.service.handler.MoneyTransferCommandHandler;
import com.fabrick.test.transaction.manager.api.service.handler.TransactionsQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.TimeZone;

/**
 * A Facade that acts as the single entry point for all business operations
 * related to the Fabrick API. It orchestrates calls to the various specialized
 * handlers, hiding the complexity of the internal implementation from the Controller layer.
 * This class replaces the previous aggregation of multiple services.
 */
@Service
@RequiredArgsConstructor
public class GbsBankingApiFacade {

    // Dependencies are now on the specific, focused handlers.
    private final BalanceQueryHandler balanceQueryHandler;
    private final TransactionsQueryHandler transactionsQueryHandler;
    private final MoneyTransferCommandHandler moneyTransferCommandHandler;

    /**
     * Retrieves the balance for a given account.
     * It delegates the entire operation to the BalanceQueryHandler.
     *
     * @param accountId The ID of the account.
     * @return The account Balance.
     */
    public Balance getBalance(String accountId) {
        // The handler is called directly with the required primitive type. No new objects needed.
        return balanceQueryHandler.handle(accountId);
    }

    /**
     * Retrieves the list of transactions for an account within a given date range.
     * It builds the Query object and delegates the operation to the TransactionsQueryHandler.
     *
     * @param accountId The ID of the account.
     * @param request   The DTO containing the search criteria (from/to dates).
     * @return A TransactionListResponse containing the list of transactions.
     */
    public TransactionListResponse getTransactions(String accountId, TransactionSearchRequest request) {
        // Creates the nested record to pass all parameters to the handler.
        var query = new TransactionsQueryHandler.Query(accountId, request);
        return transactionsQueryHandler.handle(query);
    }

    /**
     * Executes a money transfer operation.
     * It builds the Command object and delegates the operation to the MoneyTransferCommandHandler.
     *
     * @param accountId The ID of the source account.
     * @param request   The DTO containing the money transfer details.
     * @return A MoneyTransferResponse confirming the operation.
     */
    public MoneyTransferResponse transferMoney(String accountId, MoneyTransferRequest request) {
        // Creates the nested record to pass all necessary data to the handler.
        var command = new MoneyTransferCommandHandler.Command(
                accountId,
                request,
                TimeZone.getDefault().getID()
        );
        return moneyTransferCommandHandler.handle(command);
    }
}