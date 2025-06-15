package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.client.GbsBankingClient;
import com.fabrick.test.transaction.manager.api.client.dto.GbsBankingStatus;
import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.MoneyTransferRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer.MoneyTransferResponse;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingBusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTransferServiceTest {

    private final GbsBankingClient gbsBankingClient = Mockito.mock(GbsBankingClient.class);

    private final MoneyTransferService moneyTransferService = new MoneyTransferService(gbsBankingClient);

    @AfterEach
    public void tearDown() {
        Mockito.reset(gbsBankingClient);
    }

    @Test
    public void transferMoneySuccess() {
        var gbsBankingResponse = Mockito.mock(GbsBankingResponse.class);
        Mockito.when(gbsBankingClient.createMoneyTransfer(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(gbsBankingResponse);
        Mockito.when(gbsBankingResponse.getStatus()).thenReturn(GbsBankingStatus.OK);
        Mockito.when(gbsBankingResponse.getPayload()).thenReturn(new MoneyTransferResponse());

        var res = moneyTransferService.transferMoney("123", new MoneyTransferRequest());

        Assertions.assertNotNull(res);
    }

    @Test
    public void transferMoneyBp049Error() {
        var gbsBankingResponse = Mockito.mock(GbsBankingResponse.class);
        Mockito.when(gbsBankingClient.createMoneyTransfer(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(gbsBankingResponse);
        Mockito.when(gbsBankingResponse.getStatus()).thenReturn(GbsBankingStatus.KO);
        Mockito.when(gbsBankingResponse.getErrors()).thenReturn(
                List.of(
                        new GbsBankingResponse.GbsBankingError(
                                "API000",
                                "BP049"
                        )
                )
        );
        Mockito.when(gbsBankingResponse.getPayload()).thenReturn(new MoneyTransferResponse());
        var res = moneyTransferService.transferMoney("123", new MoneyTransferRequest());

        Assertions.assertNotNull(res);
    }

    @Test
    public void transferMoneyError() {
        var gbsBankingResponse = Mockito.mock(GbsBankingResponse.class);
        Mockito.when(gbsBankingClient.createMoneyTransfer(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(gbsBankingResponse);
        Mockito.when(gbsBankingResponse.getStatus()).thenReturn(GbsBankingStatus.KO);
        Mockito.when(gbsBankingResponse.getErrors()).thenReturn(
                List.of(
                        new GbsBankingResponse.GbsBankingError(
                                "API001",
                                "BP050"
                        )
                )
        );
        try {
            moneyTransferService.transferMoney("123", new MoneyTransferRequest());
            fail();
        } catch (GbsBankingBusinessException ignored) {

        }
    }
}