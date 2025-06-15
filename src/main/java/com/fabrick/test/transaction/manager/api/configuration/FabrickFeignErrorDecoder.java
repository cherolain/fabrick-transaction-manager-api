package com.fabrick.test.transaction.manager.api.configuration;

import com.fabrick.test.transaction.manager.api.client.dto.response.FabrickApiResponse;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import com.fabrick.test.transaction.manager.api.exception.FabrickApiBusinessException;
import com.fabrick.test.transaction.manager.api.exception.FabrickApiException;
import com.fabrick.test.transaction.manager.api.utils.FabrickErrorCodeMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FabrickFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus httpStatus = HttpStatus.valueOf(response.status());
        List<FabrickApiResponse.FabrickError> fabrickErrors = Collections.emptyList();

        try (InputStream body = response.body() != null ? response.body().asInputStream() : null) {
            if (body != null && body.available() > 0) {
                FabrickApiResponse<?> apiResponse = objectMapper.readValue(body, FabrickApiResponse.class);
                if (apiResponse.getErrors() != null && !apiResponse.getErrors().isEmpty()) {
                    fabrickErrors = apiResponse.getErrors();
                }
            }
        } catch (IOException e) {
            log.error("Error reading response body for methodKey: {}", methodKey, e);
            return new FabrickApiException(httpStatus, ErrorCode.UNEXPECTED_ERROR);
        }

        List<ErrorCode> internalErrorCodes =
                fabrickErrors.
                        stream().
                        map(error -> FabrickErrorCodeMapper.resolveInternalErrorCode(error, httpStatus))
                        .collect(java.util.stream.Collectors.toList());

        if (httpStatus == HttpStatus.BAD_REQUEST) {
            return new FabrickApiBusinessException(
                    fabrickErrors,
                    internalErrorCodes
            );
        }

        // Switch per status noti, senza dettagli Fabrick
        return switch (httpStatus) {
            case UNAUTHORIZED -> new FabrickApiException(httpStatus, ErrorCode.UNAUTHORIZED);
            case FORBIDDEN -> new FabrickApiException(httpStatus, ErrorCode.FORBIDDEN);
            case NOT_FOUND -> new FabrickApiException(httpStatus, ErrorCode.NOT_FOUND);
            case SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT -> new FabrickApiException(httpStatus, ErrorCode.SERVICE_UNAVAILABLE);
            default -> new FabrickApiException(httpStatus, ErrorCode.EXTERNAL_API_FAILURE);
        };
    }
}