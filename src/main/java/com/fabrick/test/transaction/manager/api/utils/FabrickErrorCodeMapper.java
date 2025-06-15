package com.fabrick.test.transaction.manager.api.utils;

import com.fabrick.test.transaction.manager.api.client.dto.response.FabrickApiResponse;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class FabrickErrorCodeMapper {

    public static ErrorCode resolveInternalErrorCode(FabrickApiResponse.FabrickError fabrickError, HttpStatus httpStatus) {
        if (fabrickError != null && fabrickError.getCode() != null) {
            String fabrickCode = fabrickError.getCode();

            return switch (fabrickCode) {
                case "API000", "REQ007", "API004" -> ErrorCode.BAD_REQUEST;
                default -> mapHttpStatusToErrorCode(httpStatus);
            };
        }
        return mapHttpStatusToErrorCode(httpStatus);
    }

    private static ErrorCode mapHttpStatusToErrorCode(HttpStatus httpStatus) {
        return switch (httpStatus) {
            case BAD_REQUEST -> // 400
                    ErrorCode.BAD_REQUEST;
            case UNAUTHORIZED -> // 401
                    ErrorCode.UNAUTHORIZED;
            case FORBIDDEN -> // 403
                    ErrorCode.FORBIDDEN;
            case NOT_FOUND -> // 404
                    ErrorCode.NOT_FOUND;
            case METHOD_NOT_ALLOWED -> // 405
                    ErrorCode.METHOD_NOT_ALLOWED;
            case INTERNAL_SERVER_ERROR -> // 500
                    ErrorCode.INTERNAL_SERVER_ERROR; // 503
            case SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT -> // 504
                    ErrorCode.SERVICE_UNAVAILABLE;
            default ->
                // Per tutti gli altri status HTTP non gestiti esplicitamente
                    ErrorCode.EXTERNAL_API_FAILURE;
        };
    }
}