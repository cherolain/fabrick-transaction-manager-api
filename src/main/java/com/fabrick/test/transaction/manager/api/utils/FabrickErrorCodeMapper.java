package com.fabrick.test.transaction.manager.api.utils;

import com.fabrick.test.transaction.manager.api.dto.response.FabrickApiResponse;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component // Questo rende la classe un Spring Bean, iniettabile in altri componenti
public class FabrickErrorCodeMapper {

    public ErrorCode resolveInternalErrorCode(FabrickApiResponse.FabrickError fabrickError, HttpStatus httpStatus) {
        if (fabrickError != null && fabrickError.getCode() != null) {
            String fabrickCode = fabrickError.getCode();

            switch (fabrickCode) {
                case "API000":
                case "REQ007":
                case "API004":
                    return ErrorCode.BAD_REQUEST;
                default:
                    return mapHttpStatusToErrorCode(httpStatus);
            }
        }
        return mapHttpStatusToErrorCode(httpStatus);
    }

    private ErrorCode mapHttpStatusToErrorCode(HttpStatus httpStatus) {
        switch (httpStatus) {
            case BAD_REQUEST: // 400
                return ErrorCode.BAD_REQUEST;
            case UNAUTHORIZED: // 401
                return ErrorCode.UNAUTHORIZED;
            case FORBIDDEN: // 403
                return ErrorCode.FORBIDDEN;
            case NOT_FOUND: // 404
                return ErrorCode.NOT_FOUND;
            case METHOD_NOT_ALLOWED: // 405
                return ErrorCode.METHOD_NOT_ALLOWED;
            case INTERNAL_SERVER_ERROR: // 500
                return ErrorCode.INTERNAL_SERVER_ERROR;
            case SERVICE_UNAVAILABLE: // 503
            case GATEWAY_TIMEOUT: // 504
                return ErrorCode.SERVICE_UNAVAILABLE;
            default:
                // Per tutti gli altri status HTTP non gestiti esplicitamente
                return ErrorCode.EXTERNAL_API_FAILURE;
        }
    }
}