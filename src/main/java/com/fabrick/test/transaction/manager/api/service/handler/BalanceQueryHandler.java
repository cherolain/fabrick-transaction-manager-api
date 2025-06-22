package com.fabrick.test.transaction.manager.api.service.handler;

import com.fabrick.test.transaction.manager.api.client.GbsBankingClient;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.balance.Balance;
import com.fabrick.test.transaction.manager.api.service.template.RequestHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BalanceQueryHandler extends RequestHandler<String, Balance, Balance> {

    private final GbsBankingClient client;

    @Override
    protected GbsBankingResponse<Balance> performAction(String accountId) {
        return client.retrieveAccountBalance(accountId);
    }

    @Override
    protected Balance mapToResponse(Balance payload) {
        return payload;
    }
}