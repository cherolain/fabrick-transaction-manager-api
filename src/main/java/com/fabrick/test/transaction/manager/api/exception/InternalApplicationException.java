package com.fabrick.test.transaction.manager.api.exception;

import lombok.Getter;

@Getter
public class InternalApplicationException extends RuntimeException {
    private final ErrorCode errorCode;

    public InternalApplicationException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public InternalApplicationException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
