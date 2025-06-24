package com.fabrick.test.transaction.manager.api.service.handler;

import com.fabrick.test.transaction.manager.api.client.GbsBankingClient;
import com.fabrick.test.transaction.manager.api.client.dto.GbsBankingStatus;
import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.MoneyTransferRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer.MoneyTransferGbsResponse;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingBusinessException;
import com.fabrick.test.transaction.manager.api.mapper.MoneyTransferMapper;
import com.fabrick.test.transaction.manager.api.dto.moneytransfer.MoneyTransferApiResponse;
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
    @Mock
    private MoneyTransferMapper moneyTransferMapper;

    private MoneyTransferCommandHandler moneyTransferCommandHandler;

    @BeforeEach
    public void setUp() {
        moneyTransferCommandHandler = new MoneyTransferCommandHandler(moneyTransferMapper, gbsBankingClient);
    }

    @Test
    public void handle_whenApiReturnsOk_shouldReturnPayload() {

        String accountId = "12345";
        var moneyTransferRequest = new MoneyTransferRequest();
        var command = new MoneyTransferCommandHandler.Command(accountId, moneyTransferRequest, TimeZone.getDefault().getID());

        var expectedPayload = new MoneyTransferGbsResponse();
        var gbsBankingResponse = new GbsBankingResponse<MoneyTransferGbsResponse>();
        gbsBankingResponse.setStatus(GbsBankingStatus.OK);
        gbsBankingResponse.setPayload(expectedPayload);

        when(gbsBankingClient.createMoneyTransfer(any(), any(), any())).thenReturn(gbsBankingResponse);

        var expectedApiResponse = new MoneyTransferApiResponse();
        when(moneyTransferMapper.toMoneyTransferApiResponse(expectedPayload)).thenReturn(expectedApiResponse);

        // Esegui il test
        MoneyTransferApiResponse actualResponse = moneyTransferCommandHandler.handle(command);

        assertNotNull(actualResponse);
        assertEquals(expectedApiResponse, actualResponse);
    }

    @Test
    public void handle_whenApiReturnsKo_shouldThrowGbsBankingBusinessException() {

        var command = new MoneyTransferCommandHandler.Command("12345", new MoneyTransferRequest(), TimeZone.getDefault().getID());

        var gbsBankingResponse = new GbsBankingResponse<MoneyTransferGbsResponse>();
        gbsBankingResponse.setStatus(GbsBankingStatus.KO);
        gbsBankingResponse.setErrors(List.of(new GbsBankingResponse.GbsBankingError("API001", "Some error")));

        when(gbsBankingClient.createMoneyTransfer(any(), any(), any())).thenReturn(gbsBankingResponse);


        GbsBankingBusinessException exception = assertThrows(GbsBankingBusinessException.class, () -> moneyTransferCommandHandler.handle(command));

        assertNotNull(exception.getErrors());
        assertEquals(1, exception.getErrors().size());
        assertEquals("API001", exception.getErrors().getFirst().getCode());
    }
}