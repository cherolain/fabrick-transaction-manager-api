package com.fabrick.test.transaction.manager.api.service.handler;

import com.fabrick.test.transaction.manager.api.client.GbsBankingClient;
import com.fabrick.test.transaction.manager.api.client.dto.GbsBankingStatus;
import com.fabrick.test.transaction.manager.api.client.dto.request.transactions.TransactionSearchRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionListResponse;
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

    private TransactionsQueryHandler transactionsQueryHandler;

    @BeforeEach
    void setUp() {
        transactionsQueryHandler = new TransactionsQueryHandler(gbsBankingClient);
    }

    @Test
    void handle_whenApiReturnsOk_shouldCallClientAndReturnPayload() {
        // --- ARRANGE ---
        String accountId = "123456";
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 1, 31);
        var searchRequest = new TransactionSearchRequest(fromDate, toDate);
        var query = new TransactionsQueryHandler.Query(accountId, searchRequest);

        // This is the expected payload from the external API
        var expectedPayload = new TransactionListResponse();
        expectedPayload.setList(Collections.emptyList()); // Set a default value for completeness

        // This is the full API response envelope
        var apiResponse = new GbsBankingResponse<TransactionListResponse>();
        apiResponse.setStatus(GbsBankingStatus.OK);
        apiResponse.setPayload(expectedPayload);

        // Configure the mock client to return the prepared response
        when(gbsBankingClient.retrieveAccountTransactions(accountId, searchRequest)).thenReturn(apiResponse);

        // --- ACT ---
        // Execute the handler with the prepared query
        TransactionListResponse result = transactionsQueryHandler.handle(query);

        // --- ASSERT ---
        // Verify that the result is not null and is exactly the payload we mocked
        assertNotNull(result);
        assertEquals(expectedPayload, result);

        // Verify that the correct method on the client was called with the correct parameters
        verify(gbsBankingClient).retrieveAccountTransactions(accountId, searchRequest);
    }
}