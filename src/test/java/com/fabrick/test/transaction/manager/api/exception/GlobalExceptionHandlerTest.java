package com.fabrick.test.transaction.manager.api.exception;

import com.fabrick.test.transaction.manager.api.model.TransactionManagerApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleGbsBankingApiException_shouldReturnCorrectUnauthorizedResponse() {
        GbsBankingApiException ex = new GbsBankingApiException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED);
        ResponseEntity<TransactionManagerApiResponse<?>> response = handler.handleGbsBankingApiException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ErrorCode.UNAUTHORIZED.getCode(), response.getBody().getErrors().getFirst().getCode());
        assertTrue(response.getBody().getErrors().getFirst().getDescription().contains("Unauthorized"));
    }

    @Test
    void handleGbsBankingBusinessException_shouldReturnBadRequest() {
        GbsBankingBusinessException ex = new GbsBankingBusinessException(
                List.of(), List.of(ErrorCode.BAD_REQUEST)
        );
        ResponseEntity<TransactionManagerApiResponse<?>> response = handler.handleGbsBankingApiBusinessException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ErrorCode.BAD_REQUEST.getCode(), response.getBody().getErrors().getFirst().getCode());
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() {
        Exception ex = new Exception("Generic error");
        ResponseEntity<TransactionManagerApiResponse<?>> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ErrorCode.UNEXPECTED_ERROR.getCode(), response.getBody().getErrors().getFirst().getCode());
    }
}