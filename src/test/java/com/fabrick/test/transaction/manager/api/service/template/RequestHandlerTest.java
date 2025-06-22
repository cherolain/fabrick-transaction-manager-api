package com.fabrick.test.transaction.manager.api.service.template;

import com.fabrick.test.transaction.manager.api.client.dto.GbsBankingStatus;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingApiException;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingBusinessException;
import com.fabrick.test.transaction.manager.api.exception.InternalApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class RequestHandlerTest {

    private StubRequestHandler spyHandler;

    static class StubRequestHandler extends RequestHandler<String, String, String> {
        @Override
        protected GbsBankingResponse<String> performAction(String request) {
            // This method is spied on, its body is irrelevant for most tests.
            return null;
        }

        @Override
        protected String mapToResponse(String payload) {
            return payload; // Simple identity mapping for the test
        }
    }

    @BeforeEach
    void setUp() {
        spyHandler = spy(new StubRequestHandler());
    }

    @Test
    void handle_whenApiReturnsOk_shouldSucceed() {
        // This test remains separate as it's the "happy path".
        var successResponse = new GbsBankingResponse<String>();
        successResponse.setStatus(GbsBankingStatus.OK);
        successResponse.setPayload("SuccessPayload");
        doReturn(successResponse).when(spyHandler).performAction(any());

        String result = spyHandler.handle("test-request");

        assertEquals("SuccessPayload", result);
    }


    @ParameterizedTest(name = "Scenario {index}: Should throw {1} when setup")
    @MethodSource("errorScenarios")
    void handle_whenErrorOccurs_shouldThrowCorrectException(
            Consumer<StubRequestHandler> scenarioSetup,
            Class<? extends Throwable> expectedExceptionClass
    ) {
        scenarioSetup.accept(spyHandler);

        assertThrows(expectedExceptionClass, () -> spyHandler.handle("test-request"));
    }

    private static Stream<Arguments> errorScenarios() {
        return Stream.of(
                // Scenario 1: API returns KO status
                Arguments.of(
                        (Consumer<StubRequestHandler>) handler -> {
                            var koResponse = new GbsBankingResponse<String>();
                            koResponse.setStatus(GbsBankingStatus.KO);
                            koResponse.setErrors(List.of());
                            doReturn(koResponse).when(handler).performAction(any());
                        },
                        GbsBankingBusinessException.class
                ),
                // Scenario 2: API returns OK but with a null payload
                Arguments.of(
                        (Consumer<StubRequestHandler>) handler -> {
                            var okWithNullPayload = new GbsBankingResponse<String>();
                            okWithNullPayload.setStatus(GbsBankingStatus.OK);
                            okWithNullPayload.setPayload(null);
                            doReturn(okWithNullPayload).when(handler).performAction(any());
                        },
                        InternalApplicationException.class
                ),
                // Scenario 3: performAction throws a GbsBankingApiException
                Arguments.of(
                        (Consumer<StubRequestHandler>) handler -> {
                            var apiException = new GbsBankingApiException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN);
                            // doAnswer is used to simulate the exception being thrown
                            doAnswer(invocation -> { throw apiException; }).when(handler).performAction(any());
                        },
                        GbsBankingApiException.class
                ),
                // Scenario 4: performAction throws an unexpected RuntimeException
                Arguments.of(
                        (Consumer<StubRequestHandler>) handler -> {
                            var runtimeException = new RuntimeException("Unexpected network issue");
                            doAnswer(invocation -> { throw runtimeException; }).when(handler).performAction(any());
                        },
                        InternalApplicationException.class
                )
        );
    }
}