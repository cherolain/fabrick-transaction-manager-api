package com.fabrick.test.transaction.manager.api.configuration;

import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingApiException;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingBusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.function.Supplier;
import java.util.stream.Stream;

class GbsBankingErrorDecoderTest {

    @ParameterizedTest
    @MethodSource("decodeScenario")
    void testDecode(Supplier<Response> responseSupplier, Class<? extends Throwable> expectedException, ErrorCode expectedErrorCode) {
        GbsBankingErrorDecoder decoder = new GbsBankingErrorDecoder(
                new ObjectMapper()
        );

        Throwable exception = decoder.decode("test", responseSupplier.get());

        Assertions.assertEquals(expectedException, exception.getClass(),
                "Expected exception type does not match" + exception.getClass().getName() + " != " + expectedException.getName()
        );

        if (exception instanceof GbsBankingApiException) {
            GbsBankingApiException apiException = (GbsBankingApiException) exception;
            Assertions.assertEquals(expectedErrorCode, apiException.getErrorCode(),
                    "Expected error code does not match: " + apiException.getErrorCode() + " != " + expectedErrorCode
            );
        }
    }

    private static Stream<Arguments> decodeScenario() {
        return Stream.of(
                Arguments.of(
                        (Supplier<Response>) () -> {
                            var mockResponse = Mockito.mock(Response.class);
                            Mockito.when(mockResponse.status()).thenReturn(400);
                            var mockBody = Mockito.mock(Response.Body.class);
                            Mockito.when(mockResponse.body()).thenReturn(mockBody);
                            try {
                                Mockito.when(mockBody.asInputStream()).thenThrow(new IOException("Mocked IOException"));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            return mockResponse;
                        },
                        GbsBankingApiException.class,
                        ErrorCode.UNEXPECTED_ERROR
                )
        );
    }
}