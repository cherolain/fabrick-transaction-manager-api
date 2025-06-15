package com.fabrick.test.transaction.manager.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FabrickApiException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final ErrorCode errorCode;

    public FabrickApiException(String message, HttpStatus httpStatus, ErrorCode errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public FabrickApiException(String message, Throwable cause, HttpStatus httpStatus, ErrorCode errorCode) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}