package com.fabrick.test.transaction.manager.api.service.handler;

import com.fabrick.test.transaction.manager.api.client.GbsBankingClient;
import com.fabrick.test.transaction.manager.api.client.dto.GbsBankingStatus;
import com.fabrick.test.transaction.manager.api.client.dto.request.transactions.TransactionSearchRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionListResponse;
import com.fabrick.test.transaction.manager.api.dto.transactions.TransactionListApiResponse;
import com.fabrick.test.transaction.manager.api.mapper.TransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionsQueryHandlerTest {

    @Mock
    private GbsBankingClient gbsBankingClient;

    @Mock
    private TransactionMapper transactionMapper;

    private TransactionsQueryHandler transactionsQueryHandler;

    @BeforeEach
    void setUp() {
        transactionsQueryHandler = new TransactionsQueryHandler(gbsBankingClient, transactionMapper);
    }

    @Test
    void handle_whenApiReturnsOk_shouldCallClientAndReturnPayload() {
        String accountId = "123456";
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 1, 31);
        var searchRequest = new TransactionSearchRequest(fromDate, toDate);
        var query = new TransactionsQueryHandler.Query(accountId, searchRequest);

        var expectedPayload = new TransactionListResponse();
        expectedPayload.setList(Collections.emptyList()); // Set a default value for completeness

        var apiResponse = new GbsBankingResponse<TransactionListResponse>();
        apiResponse.setStatus(GbsBankingStatus.OK);
        apiResponse.setPayload(expectedPayload);

        when(gbsBankingClient.retrieveAccountTransactions(accountId, searchRequest)).thenReturn(apiResponse);
        var expectedApiResponse = new TransactionListApiResponse();
        when(transactionMapper.toTransactionApiResponse(expectedPayload)).thenReturn(expectedApiResponse);

        TransactionListApiResponse result = transactionsQueryHandler.handle(query);

        assertNotNull(result);
        assertEquals(expectedApiResponse, result);

        verify(gbsBankingClient).retrieveAccountTransactions(accountId, searchRequest);
    }
}