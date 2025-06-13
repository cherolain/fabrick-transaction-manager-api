package com.fabrick.test.transaction.manager.api.exception;

import com.fabrick.test.transaction.manager.api.dto.FabrickStatus;
import org.springframework.http.HttpStatus;

public class FabrickApiForbiddenException extends FabrickApiException {
    public FabrickApiForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN, FabrickStatus.KO, ErrorCode.FORBIDDEN);
    }
}
