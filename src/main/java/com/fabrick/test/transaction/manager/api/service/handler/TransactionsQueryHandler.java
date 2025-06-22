package com.fabrick.test.transaction.manager.api.service.handler;

import com.fabrick.test.transaction.manager.api.client.GbsBankingClient;
import com.fabrick.test.transaction.manager.api.client.dto.request.transactions.TransactionSearchRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionListResponse;
import com.fabrick.test.transaction.manager.api.service.template.RequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionsQueryHandler extends RequestHandler<TransactionsQueryHandler.Query, TransactionListResponse, TransactionListResponse> {


    /**
     * A nested record to group input parameters (accountId and search criteria)
     * into a single type-safe object. This is necessary because the operation requires more than one parameter.
     */
    public record Query(String accountId, TransactionSearchRequest searchRequest) {
    }

    private final GbsBankingClient client;


    @Override
    protected GbsBankingResponse<TransactionListResponse> performAction(Query query) {
        return client.retrieveAccountTransactions(query.accountId(), query.searchRequest());
    }


    @Override
    protected TransactionListResponse mapToResponse(TransactionListResponse payload) {
        return payload;
    }
}