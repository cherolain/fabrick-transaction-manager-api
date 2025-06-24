package com.fabrick.test.transaction.manager.api.service.handler;

import com.fabrick.test.transaction.manager.api.client.GbsBankingClient;
import com.fabrick.test.transaction.manager.api.client.dto.GbsBankingStatus;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.balance.BalanceResponse;
import com.fabrick.test.transaction.manager.api.dto.balance.BalanceApiResponse;
import com.fabrick.test.transaction.manager.api.mapper.BalanceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceResponseQueryHandlerTest {

    @Mock
    private GbsBankingClient gbsBankingClient;

    @Mock
    private BalanceMapper balanceMapper;

    private BalanceQueryHandler balanceQueryHandler;

    @BeforeEach
    void setUp() {
        balanceQueryHandler = new BalanceQueryHandler(gbsBankingClient, balanceMapper);
    }

    @Test
    void handle_whenApiReturnsOk_shouldCallClientAndReturnPayload() {
        String accountId = "123";

        var expectedPayload = new BalanceResponse();
        expectedPayload.setBalance(new BigDecimal("100.50"));

        var apiResponse = new GbsBankingResponse<BalanceResponse>();
        apiResponse.setStatus(GbsBankingStatus.OK);
        apiResponse.setPayload(expectedPayload);

        when(gbsBankingClient.retrieveAccountBalance(accountId)).thenReturn(apiResponse);
        var expectedApiResponse = new BalanceApiResponse();
        expectedApiResponse.setBalance(new BigDecimal("100.50"));
        when(balanceMapper.toBalanceApiResponse(expectedPayload)).thenReturn(expectedApiResponse);


        BalanceApiResponse result = balanceQueryHandler.handle(accountId);

        assertNotNull(result);
        assertEquals(expectedApiResponse, result);
        assertEquals(new BigDecimal("100.50"), result.getBalance());

        verify(gbsBankingClient).retrieveAccountBalance(accountId);
    }
}