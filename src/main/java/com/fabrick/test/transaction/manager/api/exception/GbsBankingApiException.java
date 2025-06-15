package com.fabrick.test.transaction.manager.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GbsBankingApiException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final ErrorCode errorCode;

    public GbsBankingApiException(HttpStatus httpStatus, ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

}