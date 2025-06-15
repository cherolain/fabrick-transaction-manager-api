package com.fabrick.test.transaction.manager.api.exception;

import com.fabrick.test.transaction.manager.api.client.dto.response.FabrickApiResponse;
import lombok.Getter;
import java.util.List;

@Getter
public class FabrickApiBusinessException extends RuntimeException {
    private final List<FabrickApiResponse.FabrickError> errors;
    private final List<ErrorCode> errorCodes;

    public FabrickApiBusinessException(List<FabrickApiResponse.FabrickError> errors, List<ErrorCode> errorCodes) {
        super(errors.stream()
                .map(FabrickApiResponse.FabrickError::getDescription)
                .reduce((first, second) -> first + "; " + second)
                .orElse("Multiple errors occurred"));
        this.errors = errors;
        this.errorCodes = errorCodes;
    }
}