package com.fabrick.test.transaction.manager.api.service.handler;

import com.fabrick.test.transaction.manager.api.client.GbsBankingClient;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.balance.BalanceResponse;
import com.fabrick.test.transaction.manager.api.dto.balance.BalanceApiResponse;
import com.fabrick.test.transaction.manager.api.mapper.BalanceMapper;
import com.fabrick.test.transaction.manager.api.service.template.RequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BalanceQueryHandler extends RequestHandler<String, BalanceResponse, BalanceApiResponse> {

    private final GbsBankingClient client;
    private final BalanceMapper balanceMapper;

    @Override
    protected GbsBankingResponse<BalanceResponse> performAction(String accountId) {
        return client.retrieveAccountBalance(accountId);
    }

    @Override
    protected BalanceApiResponse mapToResponse(BalanceResponse payload) {
        return balanceMapper.toBalanceApiResponse(payload);
    }
}