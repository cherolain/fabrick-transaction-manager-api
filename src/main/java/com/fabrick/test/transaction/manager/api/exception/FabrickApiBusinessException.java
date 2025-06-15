package com.fabrick.test.transaction.manager.api.exception;

import com.fabrick.test.transaction.manager.api.dto.response.FabrickApiResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class FabrickApiBusinessException extends RuntimeException {
    private final List<FabrickApiResponse.FabrickError> errors;
    private final ErrorCode errorCode;

    public FabrickApiBusinessException(String message, List<FabrickApiResponse.FabrickError> errors, ErrorCode errorCode) {
        super(message);
        this.errors = errors;
        this.errorCode = errorCode;
    }
}