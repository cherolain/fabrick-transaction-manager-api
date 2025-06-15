package com.fabrick.test.transaction.manager.api.configuration;

import com.fabrick.test.transaction.manager.api.dto.response.FabrickApiResponse;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import com.fabrick.test.transaction.manager.api.exception.FabrickApiBusinessException;
import com.fabrick.test.transaction.manager.api.exception.FabrickApiException;
import com.fabrick.test.transaction.manager.api.utils.FabrickErrorCodeMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
public class FabrickFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;
    private final FabrickErrorCodeMapper fabrickErrorCodeMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus httpStatus = HttpStatus.valueOf(response.status());
        FabrickApiResponse.FabrickError fabrickError = null;

        try (InputStream body = response.body() != null ? response.body().asInputStream() : null) {
            if (body != null && body.available() > 0) {
                FabrickApiResponse<?> apiResponse = objectMapper.readValue(body, FabrickApiResponse.class);
                if (apiResponse.getErrors() != null && !apiResponse.getErrors().isEmpty()) {
                    fabrickError = apiResponse.getErrors().getFirst();
                }
            }
        } catch (IOException e) {
            return new FabrickApiException("Failed to parse Fabrick error response.", httpStatus, ErrorCode.EXTERNAL_API_FAILURE);
        }

        ErrorCode internalErrorCode = fabrickErrorCodeMapper.resolveInternalErrorCode(fabrickError, httpStatus);

        if (httpStatus == HttpStatus.BAD_REQUEST && fabrickError != null) {
            return new FabrickApiBusinessException(
                    fabrickError.getDescription(),
                    java.util.List.of(fabrickError),
                    internalErrorCode
            );
        }

        // Switch per status noti, senza dettagli Fabrick
        return switch (httpStatus) {
            case UNAUTHORIZED -> new FabrickApiException(ErrorCode.UNAUTHORIZED.getDefaultMessage(), httpStatus, ErrorCode.UNAUTHORIZED);
            case FORBIDDEN -> new FabrickApiException(ErrorCode.FORBIDDEN.getDefaultMessage(), httpStatus, ErrorCode.FORBIDDEN);
            case NOT_FOUND -> new FabrickApiException(ErrorCode.NOT_FOUND.getDefaultMessage(), httpStatus, ErrorCode.NOT_FOUND);
            case INTERNAL_SERVER_ERROR -> new FabrickApiException(ErrorCode.INTERNAL_SERVER_ERROR.getDefaultMessage(), httpStatus, ErrorCode.EXTERNAL_API_FAILURE);
            case SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT -> new FabrickApiException(ErrorCode.SERVICE_UNAVAILABLE.getDefaultMessage(), httpStatus, ErrorCode.SERVICE_UNAVAILABLE);
            default -> new FabrickApiException(ErrorCode.EXTERNAL_API_FAILURE.getDefaultMessage(), httpStatus, ErrorCode.EXTERNAL_API_FAILURE);
        };
    }
}