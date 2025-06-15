package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.client.GbsBankingClient;
import com.fabrick.test.transaction.manager.api.client.dto.GbsBankingStatus;
import com.fabrick.test.transaction.manager.api.client.dto.request.transactions.TransactionSearchRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionListResponse;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingApiException;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingBusinessException;
import com.fabrick.test.transaction.manager.api.exception.InternalApplicationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

class TransactionServiceTest {
    private final GbsBankingClient gbsBankingClient = Mockito.mock(GbsBankingClient.class);

    private final TransactionService transactionService = new TransactionService(gbsBankingClient);

    @AfterEach
    public void tearDown() {
        Mockito.reset(gbsBankingClient);
    }

    @Test
    public void testCreateTransactionSuccess() {
        var gbsBankingResponse = Mockito.mock(GbsBankingResponse.class);
        Mockito.when(gbsBankingClient.retrieveAccountTransactions(Mockito.any(), Mockito.any())).thenReturn(gbsBankingResponse);
        Mockito.when(gbsBankingResponse.getStatus()).thenReturn(GbsBankingStatus.OK);
        Mockito.when(gbsBankingResponse.getPayload()).thenReturn(new TransactionListResponse());

        var res = transactionService.getTransactions("123", new TransactionSearchRequest());
        Assertions.assertNotNull(res);
    }

    @Test
    public void testCreateTransactionNoPayload() {
        var gbsBankingResponse = Mockito.mock(GbsBankingResponse.class);
        Mockito.when(gbsBankingClient.retrieveAccountTransactions(Mockito.any(), Mockito.any())).thenReturn(gbsBankingResponse);
        Mockito.when(gbsBankingResponse.getStatus()).thenReturn(GbsBankingStatus.OK);
        Mockito.when(gbsBankingResponse.getPayload()).thenReturn(null);

        try {
            transactionService.getTransactions("123", new TransactionSearchRequest());
            Assertions.fail();
        } catch (InternalApplicationException ignored) {
        }
    }

    @Test
    public void testCreateTransactionKO() {
        var gbsBankingResponse = Mockito.mock(GbsBankingResponse.class);
        Mockito.when(gbsBankingClient.retrieveAccountTransactions(Mockito.any(), Mockito.any())).thenReturn(gbsBankingResponse);
        Mockito.when(gbsBankingResponse.getStatus()).thenReturn(GbsBankingStatus.KO);
        Mockito.when(gbsBankingResponse.getErrors()).thenReturn(
                List.of(
                        new GbsBankingResponse.GbsBankingError(
                                "aCode", "aDesc"
                        )
                )
        );

        try {
            transactionService.getTransactions("123", new TransactionSearchRequest());
            Assertions.fail();
        } catch (GbsBankingBusinessException ignored) {
        }

    }

    @ParameterizedTest
    @MethodSource("errorScenarios")
    public void getTransactionsError(
            Consumer<GbsBankingClient> setUpScenario,
            Class<? extends Throwable> expectedException
    ) {
        setUpScenario.accept(gbsBankingClient);

        try {
            transactionService.getTransactions("123", new TransactionSearchRequest());
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals(expectedException, e.getClass());
        }
    }

    private static Stream<Arguments> errorScenarios() {
        return Stream.of(
                Arguments.of(
                        (Consumer<GbsBankingClient>) gbsBankingClient -> Mockito.when(gbsBankingClient.retrieveAccountTransactions(
                                Mockito.any(),
                                Mockito.any())
                        ).thenThrow(
                                new GbsBankingBusinessException(List.of(), List.of())
                        ),
                        GbsBankingBusinessException.class
                ),
                Arguments.of(
                        (Consumer<GbsBankingClient>) gbsBankingClient -> Mockito.when(gbsBankingClient.retrieveAccountTransactions(
                                Mockito.any(),
                                Mockito.any())
                        ).thenThrow(
                                new GbsBankingApiException(HttpStatus.OK, ErrorCode.BAD_REQUEST)
                        ),
                        GbsBankingApiException.class
                )
        );
    }

}