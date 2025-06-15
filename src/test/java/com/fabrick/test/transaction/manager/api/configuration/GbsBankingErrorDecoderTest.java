package com.fabrick.test.transaction.manager.api.configuration;

import com.fabrick.test.transaction.manager.api.client.dto.GbsBankingStatus;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
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
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

class GbsBankingErrorDecoderTest {

    @ParameterizedTest
    @MethodSource("decodeScenario")
    void testDecode(
            Supplier<Response> responseSupplier,
            Supplier<ObjectMapper> objectMapperSupplier,
            Class<? extends Throwable> expectedException,
            List<ErrorCode> expectedErrorCodes
    ) {
        GbsBankingErrorDecoder decoder = new GbsBankingErrorDecoder(objectMapperSupplier.get());

        Throwable exception = decoder.decode("test", responseSupplier.get());

        Assertions.assertEquals(expectedException, exception.getClass(),
                "Expected exception type does not match" + exception.getClass().getName() + " != " + expectedException.getName()
        );

        Assertions.assertEquals(expectedErrorCodes, getErrorCodes(exception),
                "Expected error code does not match: " + getErrorCodes(exception) + " != " + expectedErrorCodes
        );
    }

    private static List<ErrorCode> getErrorCodes(Throwable exception) {
        return switch (exception.getClass().getSimpleName()) {
            case "GbsBankingApiException" -> List.of(((GbsBankingApiException) exception).getErrorCode());
            case "GbsBankingBusinessException" -> ((GbsBankingBusinessException) exception).getErrorCodes();
            default -> List.of();
        };
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
                        (Supplier<ObjectMapper>) ObjectMapper::new,
                        GbsBankingApiException.class,
                        List.of(ErrorCode.UNEXPECTED_ERROR)
                ),
                Arguments.of(
                        (Supplier<Response>) () -> {
                            var mockResponse = Mockito.mock(Response.class);
                            Mockito.when(mockResponse.status()).thenReturn(400);
                            var mockBody = Mockito.mock(Response.Body.class);
                            Mockito.when(mockResponse.body()).thenReturn(mockBody);
                            try {
                                var mockInputStream = Mockito.mock(InputStream.class);
                                Mockito.when(mockBody.asInputStream()).thenReturn(mockInputStream);
                                Mockito.when(mockInputStream.available()).thenReturn(1);
                            } catch (IOException ignored) {
                            }
                            return mockResponse;
                        },
                        (Supplier<ObjectMapper>) () -> {
                            var mapper = Mockito.mock(ObjectMapper.class);
                            try {
                                Mockito.when(mapper.readValue(Mockito.any(InputStream.class), Mockito.eq(GbsBankingResponse.class))).thenReturn(
                                        new GbsBankingResponse<>(
                                                GbsBankingStatus.KO,
                                                List.of(
                                                        new GbsBankingResponse.GbsBankingError(
                                                                "errorCode",
                                                                "errorDesc"
                                                        )
                                                ),
                                                null
                                        )
                                );
                            } catch (Exception ignored) {

                            }
                            return mapper;
                        },
                        GbsBankingBusinessException.class,
                        List.of(ErrorCode.BAD_REQUEST)
                ),
                getArgumentWithStatusAndErrorCode(403, ErrorCode.FORBIDDEN),
                getArgumentWithStatusAndErrorCode(401, ErrorCode.UNAUTHORIZED),
                getArgumentWithStatusAndErrorCode(404, ErrorCode.NOT_FOUND),
                getArgumentWithStatusAndErrorCode(503, ErrorCode.SERVICE_UNAVAILABLE),
                getArgumentWithStatusAndErrorCode(504, ErrorCode.SERVICE_UNAVAILABLE),
                getArgumentWithStatusAndErrorCode(505, ErrorCode.EXTERNAL_API_FAILURE)
        );
    }

    private static Arguments getArgumentWithStatusAndErrorCode(int t, ErrorCode unauthorized) {
        return Arguments.of(
                (Supplier<Response>) () -> {
                    var mockResponse = Mockito.mock(Response.class);
                    Mockito.when(mockResponse.status()).thenReturn(t);
                    var mockBody = Mockito.mock(Response.Body.class);
                    Mockito.when(mockResponse.body()).thenReturn(mockBody);
                    return mockResponse;
                },
                (Supplier<ObjectMapper>) ObjectMapper::new,
                GbsBankingApiException.class,
                List.of(unauthorized)
        );
    }
}