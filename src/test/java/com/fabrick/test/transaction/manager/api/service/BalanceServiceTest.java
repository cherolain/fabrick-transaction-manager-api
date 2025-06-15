package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.client.GbsBankingClient;
import com.fabrick.test.transaction.manager.api.client.dto.GbsBankingStatus;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.balance.Balance;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingApiException;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingBusinessException;
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

class BalanceServiceTest {

    private final GbsBankingClient gbsBankingClient = Mockito.mock(GbsBankingClient.class);

    private final BalanceService balanceService = new BalanceService(gbsBankingClient);


    @AfterEach
    public void tearDown() {
        Mockito.reset(gbsBankingClient);
    }

    @Test
    public void getAccountBalanceSuccess() {
        var gbsBankingResponse = Mockito.mock(GbsBankingResponse.class);
        Mockito.when(gbsBankingClient.retrieveAccountBalance(Mockito.any()))
                .thenReturn(gbsBankingResponse);

        Mockito.when(gbsBankingResponse.getStatus()).thenReturn(GbsBankingStatus.OK);
        Balance balance = new Balance();
        Mockito.when(gbsBankingResponse.getPayload()).thenReturn(balance);

        var res = balanceService.getAccountBalance("123");

        Assertions.assertEquals(balance, res);
    }

    @Test
    public void getAccountBalanceNotOK() {
        var gbsBankingResponse = Mockito.mock(GbsBankingResponse.class);
        Mockito.when(gbsBankingClient.retrieveAccountBalance(Mockito.any()))
                .thenReturn(gbsBankingResponse);

        Mockito.when(gbsBankingResponse.getStatus()).thenReturn(GbsBankingStatus.PENDING);
        Balance balance = new Balance();
        Mockito.when(gbsBankingResponse.getErrors()).thenReturn(
                List.of(
                        new GbsBankingResponse.GbsBankingError(
                                "Acode",
                                "Adesc"
                        ))
        );

        try {
            balanceService.getAccountBalance("123");
            Assertions.fail();
        } catch (GbsBankingBusinessException e) {

        }
    }

    @ParameterizedTest
    @MethodSource("errorScenarios")
    public void getAccountBalanceError(
            Consumer<GbsBankingClient> setUpScenario,
            Class<? extends Throwable> expectedException
    ) {
        setUpScenario.accept(gbsBankingClient);

        try {
            balanceService.getAccountBalance("123");
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals(expectedException, e.getClass());
        }
    }

    private static Stream<Arguments> errorScenarios() {
        return Stream.of(
                Arguments.of(
                        (Consumer<GbsBankingClient>) gbsBankingClient -> Mockito.when(gbsBankingClient.retrieveAccountBalance(Mockito.any())).thenThrow(
                                new GbsBankingBusinessException(List.of(), List.of())
                        ),
                        GbsBankingBusinessException.class
                ),
                Arguments.of(
                        (Consumer<GbsBankingClient>) gbsBankingClient -> Mockito.when(gbsBankingClient.retrieveAccountBalance(Mockito.any())).thenThrow(
                                new GbsBankingApiException(HttpStatus.OK, ErrorCode.BAD_REQUEST)
                        ),
                        GbsBankingApiException.class
                )
        );
    }

}