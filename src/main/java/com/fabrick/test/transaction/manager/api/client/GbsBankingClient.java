package com.fabrick.test.transaction.manager.api.client;

import com.fabrick.test.transaction.manager.api.configuration.GbsBankingConfiguration;
import com.fabrick.test.transaction.manager.api.client.dto.request.transactions.TransactionSearchRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.balance.Balance;
import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.MoneyTransferRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer.MoneyTransferResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "gbsBanking", configuration = GbsBankingConfiguration.class)
public interface GbsBankingClient {
    /**
     * Base URL for the GbsBanking API.
     */
    String BALANCE_ENDPOINT = "/api/gbs/banking/v4.0/accounts/{accountId}/balance";
    String MONEY_TRANSFER_ENDPOINT = "/api/gbs/banking/v4.0/accounts/{accountId}/payments/money-transfers";
    String TRANSACTION_ENDPOINT = "/api/gbs/banking/v4.0/accounts/{accountId}/transactions";

    /**
     * Retrieves the balance of a specified account.
     *
     * @param accountId The unique identifier of the account for which the balance is requested.
     * @return A {@link GbsBankingResponse} containing the {@link Balance} details of the specified account.
     */
    @RequestMapping(method = RequestMethod.GET, value = BALANCE_ENDPOINT)
    GbsBankingResponse<Balance> retrieveAccountBalance(@PathVariable String accountId);


    /**
     * Creates a money transfer for a specified account.
     *
     * @param accountId The unique identifier of the account from which the money transfer is initiated.
     * @param moneyTransferResponseRequest The request object containing the details of the money transfer to be created.
     * @return A {@link GbsBankingResponse} containing the {@link MoneyTransferResponse} with the details of the created money transfer.
     */
    @RequestMapping(method = RequestMethod.POST, value = MONEY_TRANSFER_ENDPOINT)
    GbsBankingResponse<MoneyTransferResponse> createMoneyTransfer(
            @PathVariable String accountId,
            @RequestBody MoneyTransferRequest moneyTransferResponseRequest,
            @RequestHeader("X-Time-Zone") String timeZoneHeader
    );

    /**
     * Retrieves a list of transactions for a specified account within a given date range.
     *
     * @param accountId The unique identifier of the account for which transactions are requested.
     * @param searchRequest The request object containing the date range and other search parameters for filtering transactions.
     * @return A {@link GbsBankingResponse} containing a {@link TransactionListResponse} with the list of transactions matching the search criteria.
     */
    @RequestMapping(method = RequestMethod.GET, value = TRANSACTION_ENDPOINT)
    GbsBankingResponse<TransactionListResponse> retrieveAccountTransactions(
            @PathVariable String accountId,
            @SpringQueryMap TransactionSearchRequest searchRequest
    );
}