package com.fabrick.test.transaction.manager.api.configuration;

import com.fabrick.test.transaction.manager.api.dto.FabrickStatus;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import com.fabrick.test.transaction.manager.api.exception.FabrickApiException;
import com.fabrick.test.transaction.manager.api.exception.FabrickApiForbiddenException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FabrickFeignErrorDecoder implements ErrorDecoder {
    private static final Pattern STATUS_PATTERN = Pattern.compile("\"status\"\\s*:\\s*\"(\\w+)\"");

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        String responseBody = extractResponseBody(response);

        // Log error details
        log.error("Feign client error. Method: {}, Status: {}, Body: {}",
                methodKey, status, responseBody);

        FabrickStatus fabrickStatus = extractFabrickStatus(responseBody);

        // Map status codes to exceptions
        switch (status) {
            case BAD_REQUEST:
                return new FabrickApiException(ErrorCode.EXTERNAL_API_FAILURE.getDefaultMessage(), status, fabrickStatus,ErrorCode.EXTERNAL_API_FAILURE);
            case UNAUTHORIZED:
                return new FabrickApiException(ErrorCode.UNAUTHORIZED.getDefaultMessage(), status, fabrickStatus, ErrorCode.UNAUTHORIZED);
            case FORBIDDEN:
                return new FabrickApiForbiddenException(ErrorCode.FORBIDDEN.getDefaultMessage());
            case NOT_FOUND:
                return new FabrickApiException(ErrorCode.NOT_FOUND.getDefaultMessage(), status, fabrickStatus, ErrorCode.NOT_FOUND);
            case INTERNAL_SERVER_ERROR:
                return new FabrickApiException(ErrorCode.EXTERNAL_API_FAILURE.getDefaultMessage(), status, fabrickStatus, ErrorCode.EXTERNAL_API_FAILURE);
            case SERVICE_UNAVAILABLE:
                return new FabrickApiException(ErrorCode.SERVICE_UNAVAILABLE.getDefaultMessage(), status, fabrickStatus, ErrorCode.SERVICE_UNAVAILABLE);
            default:
                return new Exception("Unexpected error occurred. Please contact support.");
        }
    }

    private String extractResponseBody(Response response) {
        if (response.body() == null) {
            return "No response body";
        }

        try {
            return new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            log.error("Failed to read response body", ex);
            return "Error reading response body";
        }
    }

    private FabrickStatus extractFabrickStatus(String responseBody) {
        Matcher matcher = STATUS_PATTERN.matcher(responseBody);
        if (matcher.find()) {
            return FabrickStatus.valueOf(matcher.group(1));
        }
        return null;
    }

}
