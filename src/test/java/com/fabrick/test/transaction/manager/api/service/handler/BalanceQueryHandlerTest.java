package com.fabrick.test.transaction.manager.api.service.handler;

import com.fabrick.test.transaction.manager.api.client.GbsBankingClient;
import com.fabrick.test.transaction.manager.api.client.dto.GbsBankingStatus;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.balance.Balance;
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
class BalanceQueryHandlerTest {

    @Mock
    private GbsBankingClient gbsBankingClient;

    private BalanceQueryHandler balanceQueryHandler;

    @BeforeEach
    void setUp() {
        balanceQueryHandler = new BalanceQueryHandler(gbsBankingClient);
    }

    @Test
    void handle_whenApiReturnsOk_shouldCallClientAndReturnPayload() {
        String accountId = "123";

        var expectedPayload = new Balance();
        expectedPayload.setBalance(new BigDecimal("100.50"));

        var apiResponse = new GbsBankingResponse<Balance>();
        apiResponse.setStatus(GbsBankingStatus.OK);
        apiResponse.setPayload(expectedPayload);

        when(gbsBankingClient.retrieveAccountBalance(accountId)).thenReturn(apiResponse);

        Balance result = balanceQueryHandler.handle(accountId);

        assertNotNull(result);
        assertEquals(expectedPayload, result);
        assertEquals(new BigDecimal("100.50"), result.getBalance());

        verify(gbsBankingClient).retrieveAccountBalance(accountId);
    }
}