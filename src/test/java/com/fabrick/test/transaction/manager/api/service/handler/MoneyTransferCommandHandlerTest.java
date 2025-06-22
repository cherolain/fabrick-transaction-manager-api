package com.fabrick.test.transaction.manager.api.service.handler;

import com.fabrick.test.transaction.manager.api.client.GbsBankingClient;
import com.fabrick.test.transaction.manager.api.client.dto.GbsBankingStatus;
import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.MoneyTransferRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer.MoneyTransferResponse;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingBusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MoneyTransferCommandHandlerTest {

    @Mock
    private GbsBankingClient gbsBankingClient;

    private MoneyTransferCommandHandler moneyTransferCommandHandler;

    @BeforeEach
    public void setUp() {
        moneyTransferCommandHandler = new MoneyTransferCommandHandler(gbsBankingClient);
    }

    @Test
    public void handle_whenApiReturnsOk_shouldReturnPayload() {
        // --- ARRANGE ---

        // 1. Definiamo l'input per il nostro handler
        String accountId = "12345";
        var moneyTransferRequest = new MoneyTransferRequest();
        var command = new MoneyTransferCommandHandler.Command(accountId, moneyTransferRequest, TimeZone.getDefault().getID());

        // 2. Mock della risposta dell'API esterna
        var expectedPayload = new MoneyTransferResponse();
        var gbsBankingResponse = new GbsBankingResponse<MoneyTransferResponse>();
        gbsBankingResponse.setStatus(GbsBankingStatus.OK);
        gbsBankingResponse.setPayload(expectedPayload);

        when(gbsBankingClient.createMoneyTransfer(any(), any(), any())).thenReturn(gbsBankingResponse);


        MoneyTransferResponse actualResponse = moneyTransferCommandHandler.handle(command);


        assertNotNull(actualResponse);
        assertEquals(expectedPayload, actualResponse);
    }

    @Test
    public void handle_whenApiReturnsKo_shouldThrowGbsBankingBusinessException() {

        var command = new MoneyTransferCommandHandler.Command("12345", new MoneyTransferRequest(), TimeZone.getDefault().getID());

        var gbsBankingResponse = new GbsBankingResponse<MoneyTransferResponse>();
        gbsBankingResponse.setStatus(GbsBankingStatus.KO);
        gbsBankingResponse.setErrors(List.of(new GbsBankingResponse.GbsBankingError("API001", "Some error")));

        when(gbsBankingClient.createMoneyTransfer(any(), any(), any())).thenReturn(gbsBankingResponse);


        GbsBankingBusinessException exception = assertThrows(GbsBankingBusinessException.class, () -> moneyTransferCommandHandler.handle(command));

        assertNotNull(exception.getErrors());
        assertEquals(1, exception.getErrors().size());
        assertEquals("API001", exception.getErrors().getFirst().getCode());
    }
}