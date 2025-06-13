package com.fabrick.test.transaction.manager.api.exception;

import com.fabrick.test.transaction.manager.api.dto.FabrickStatus;
import org.springframework.http.HttpStatus;

public class FabrickApiException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final FabrickStatus fabrickStatus;
    private final ErrorCode errorCode;


    public FabrickApiException(String message, HttpStatus httpStatus, FabrickStatus fabrickStatus, ErrorCode errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.fabrickStatus = fabrickStatus;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public FabrickStatus getFabrickStatus() {
        return fabrickStatus;
    }
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}