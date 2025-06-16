package com.fabrick.test.transaction.manager.api.exception;

import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.model.TransactionManagerApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(GbsBankingApiException.class)
    public ResponseEntity<TransactionManagerApiResponse<?>> handleGbsBankingApiException(GbsBankingApiException ex) {
        log.error("GbsBanking API HTTP error: {}. Status: {}. ErrorCode: {}", ex.getMessage(), ex.getHttpStatus(), ex.getErrorCode(), ex);

        return ResponseEntity.status(ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR).body(
                new TransactionManagerApiResponse<>(
                        null,
                        List.of(new TransactionManagerApiResponse.TransactionManagerError(
                                ex.getErrorCode() != null ? ex.getErrorCode().getCode() : ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                                ex.getMessage()
                        ))
                )
        );
    }

    @ExceptionHandler(GbsBankingBusinessException.class)
    public ResponseEntity<TransactionManagerApiResponse<?>> handleGbsBankingApiBusinessException(GbsBankingBusinessException ex) {
        log.warn("GbsBanking business error: '{}'. Details: {}", ex.getMessage(), ex.getErrors(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new TransactionManagerApiResponse<>(
                        null,
                        getTransactionManagerErrors(ex)
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<TransactionManagerApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String validationDetails = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getObjectName() + ": " + error.getDefaultMessage();
                })
                .collect(Collectors.joining("; "));

        log.warn("Request validation failed: {}", validationDetails, ex);

        String clientMessage = ErrorCode.VALIDATION_ERROR.getDefaultMessage() + " " + validationDetails;
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new TransactionManagerApiResponse<>(
                        null,
                        List.of(new TransactionManagerApiResponse.TransactionManagerError(
                                ErrorCode.VALIDATION_ERROR.getCode(),
                                clientMessage
                        ))
                )
        );
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<TransactionManagerApiResponse<?>> handleConstraintViolation(ConstraintViolationException ex) {
        String constraintDetails = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));

        log.warn("Constraint violation for request parameters: {}", constraintDetails, ex);
        String clientMessage = ErrorCode.INVALID_PATH_VARIABLE.getDefaultMessage() + " " + constraintDetails;
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new TransactionManagerApiResponse<>(
                        null,
                        List.of(new TransactionManagerApiResponse.TransactionManagerError(
                                ErrorCode.INVALID_PATH_VARIABLE.getCode(),
                                clientMessage
                        ))
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<TransactionManagerApiResponse<?>> handleGenericException(Exception ex) {
        log.error("An unhandled exception occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new TransactionManagerApiResponse<>(
                        null,
                        List.of(new TransactionManagerApiResponse.TransactionManagerError(
                                ErrorCode.UNEXPECTED_ERROR.getCode(),
                                ErrorCode.UNEXPECTED_ERROR.getDefaultMessage()
                        ))
                )
        );
    }


    private static List<TransactionManagerApiResponse.TransactionManagerError> getTransactionManagerErrors(GbsBankingBusinessException ex) {
        List<GbsBankingResponse.GbsBankingError> apiErrors = ex.getErrors();
        List<ErrorCode> errorCodes = ex.getErrorCodes();
        return java.util.stream.IntStream.range(0, errorCodes.size())
                .mapToObj(i -> new TransactionManagerApiResponse.TransactionManagerError(
                        errorCodes.get(i).getCode(),
                        apiErrors != null && apiErrors.size() > i
                                ? java.util.Objects.requireNonNullElse(apiErrors.get(i).getDescription(), errorCodes.get(i).getDefaultMessage())
                                : errorCodes.get(i).getDefaultMessage()
                ))
                .toList();
    }
}