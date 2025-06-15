package com.fabrick.test.transaction.manager.api.utils;

import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

class GbsBankingPaymentsErrorCodeMapperTest {

    @ParameterizedTest
    @MethodSource("testScenario")
    public void resolveInternalErrorCode(
            GbsBankingResponse.GbsBankingError error,
            HttpStatus httpStatus,
            ErrorCode expectedErrorCode
    ) {
        Assertions.assertEquals(
                expectedErrorCode,
                GbsBankingPaymentsErrorCodeMapper.resolveInternalErrorCode(
                        error,
                        httpStatus
                ));


    }

    static Stream<Arguments> testScenario() {
        return Stream.of(
                Arguments.of(
                        new GbsBankingResponse.GbsBankingError(
                                "REQ007",
                                "aDesc"
                        ),
                        HttpStatus.BAD_REQUEST,
                        ErrorCode.BAD_REQUEST
                ),
                Arguments.of(
                        new GbsBankingResponse.GbsBankingError(
                                "REQ005",
                                "aDesc"
                        ),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorCode.INTERNAL_SERVER_ERROR
                ),
                getArgumentWithStatusAndErrorCode(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        ErrorCode.SERVICE_UNAVAILABLE
                ),
                getArgumentWithStatusAndErrorCode(
                        HttpStatus.METHOD_NOT_ALLOWED,
                        ErrorCode.METHOD_NOT_ALLOWED
                ),
                getArgumentWithStatusAndErrorCode(
                        HttpStatus.FORBIDDEN,
                        ErrorCode.FORBIDDEN
                ),
                getArgumentWithStatusAndErrorCode(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.NOT_FOUND
                ),
                getArgumentWithStatusAndErrorCode(
                        HttpStatus.UNAUTHORIZED,
                        ErrorCode.UNAUTHORIZED
                )
        );
    }

    private static Arguments getArgumentWithStatusAndErrorCode(
            HttpStatus httpStatus,
            ErrorCode errorCode
    ) {
        return Arguments.of(
                null,
                httpStatus,
                errorCode
        );
    }
}