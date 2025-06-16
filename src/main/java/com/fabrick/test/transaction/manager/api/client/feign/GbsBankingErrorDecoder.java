package com.fabrick.test.transaction.manager.api.client.feign;

import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingBusinessException;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingApiException;
import com.fabrick.test.transaction.manager.api.utils.GbsBankingErrorCodeMapper;
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
public class GbsBankingErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus httpStatus = HttpStatus.valueOf(response.status());
        List<GbsBankingResponse.GbsBankingError> gbsBankingErrors = Collections.emptyList();

        try (InputStream body = response.body() != null ? response.body().asInputStream() : null) {
            if (body != null && body.available() > 0) {
                GbsBankingResponse<?> apiResponse = objectMapper.readValue(body, GbsBankingResponse.class);
                if (apiResponse.getErrors() != null && !apiResponse.getErrors().isEmpty()) {
                    gbsBankingErrors = apiResponse.getErrors();
                }
            }
        } catch (IOException e) {
            log.error("Error reading response body for methodKey: {}", methodKey, e);
            return new GbsBankingApiException(httpStatus, ErrorCode.UNEXPECTED_ERROR);
        }

        List<ErrorCode> internalErrorCodes =
                gbsBankingErrors.
                        stream().
                        map(error -> GbsBankingErrorCodeMapper.resolveInternalErrorCode(error, httpStatus))
                        .collect(java.util.stream.Collectors.toList());

        if (httpStatus == HttpStatus.BAD_REQUEST) {
            return new GbsBankingBusinessException(
                    gbsBankingErrors,
                    internalErrorCodes
            );
        }

        // Switch per status noti, senza dettagli GbsBanking
        return switch (httpStatus) {
            case UNAUTHORIZED -> new GbsBankingApiException(httpStatus, ErrorCode.UNAUTHORIZED);
            case FORBIDDEN -> new GbsBankingApiException(httpStatus, ErrorCode.FORBIDDEN);
            case NOT_FOUND -> new GbsBankingApiException(httpStatus, ErrorCode.NOT_FOUND);
            case SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT -> new GbsBankingApiException(httpStatus, ErrorCode.SERVICE_UNAVAILABLE);
            default -> new GbsBankingApiException(httpStatus, ErrorCode.EXTERNAL_API_FAILURE);
        };
    }
}