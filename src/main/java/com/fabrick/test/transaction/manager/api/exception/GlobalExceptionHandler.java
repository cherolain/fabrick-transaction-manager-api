package com.fabrick.test.transaction.manager.api.exception;

import com.fabrick.test.transaction.manager.api.dto.FabrickApiResponse.FabrickError;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice // Dice a Spring: "Ascolta le eccezioni lanciate dai controller"
@Slf4j
public class GlobalExceptionHandler {

    // compact immutable class data-only objects (DTOs)
    public record ErrorResponse(String code, String description, List<FabrickError> details) {}


    // Gestisce le eccezioni di Fabrick API
    @ExceptionHandler(FabrickApiException.class)
    public ResponseEntity<ErrorResponse> handleFabrickApiException(FabrickApiException ex) {
        log.error("Fabrick API failure: {}", ex.getMessage(), ex.getCause());
        ErrorCode errorCode = ex.getErrorCode();
        return buildErrorResponseEntity(
                errorCode.getCode(),
                errorCode.getDefaultMessage(),
                null,
                ex.getHttpStatus()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String description = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String field = ((FieldError) error).getField();
                    return field + ": " + error.getDefaultMessage();
                })
                .collect(Collectors.joining("; "));
        log.warn("Validation failed for request: {}", description);
        return buildErrorResponseEntity(
                ErrorCode.VALIDATION_ERROR.getCode(),
                description,
                null,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        log.warn("No resource found: {}", ex.getMessage());
        return buildErrorResponseEntity(
                ErrorCode.NOT_FOUND.getCode(),
                "Resource not found or missing required path variable.",
                null,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String description = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));
        log.warn("Constraint violation for request parameters: {}", description);
        return buildErrorResponseEntity(
                ErrorCode.INVALID_PATH_VARIABLE.getCode(),
                description,
                null,
                HttpStatus.BAD_REQUEST
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponseEntity(String code, String description, List<FabrickError> details, HttpStatus status) {
        return new ResponseEntity<>(new ErrorResponse(code, description, details), status);
    }
}