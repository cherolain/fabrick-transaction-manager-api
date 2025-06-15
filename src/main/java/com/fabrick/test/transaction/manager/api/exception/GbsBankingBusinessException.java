package com.fabrick.test.transaction.manager.api.exception;

import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import lombok.Getter;
import java.util.List;

@Getter
public class GbsBankingBusinessException extends RuntimeException {
    private final List<GbsBankingResponse.GbsBankingError> errors;
    private final List<ErrorCode> errorCodes;

    public GbsBankingBusinessException(List<GbsBankingResponse.GbsBankingError> errors, List<ErrorCode> errorCodes) {
        super(errors.stream()
                .map(GbsBankingResponse.GbsBankingError::getDescription)
                .reduce((first, second) -> first + "; " + second)
                .orElse("Multiple errors occurred"));
        this.errors = errors;
        this.errorCodes = errorCodes;
    }
}