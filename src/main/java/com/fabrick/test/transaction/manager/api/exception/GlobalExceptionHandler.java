package com.fabrick.test.transaction.manager.api.exception;

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

    // ---
    // Handler per FabrickApiException
    // Gestisce errori di comunicazione HTTP (es. 400, 401, 403, 404, 500, 503) direttamente da Fabrick,
    // come tradotti dal FabrickFeignErrorDecoder.
    // ---
    @ExceptionHandler(FabrickApiException.class)
    public ResponseEntity<TransactionManagerApiResponse<?>> handleFabrickApiException(FabrickApiException ex) {
        // ex.getMessage() contiene il messaggio dettagliato costruito nel FabrickFeignErrorDecoder
        // (es. "Fabrick API returned an error: Unauthorized. Details: Credenziali API non valide o scadute")
        log.error("Fabrick API HTTP error: {}. Status: {}. ErrorCode: {}", ex.getMessage(), ex.getHttpStatus(), ex.getErrorCode(), ex);

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

    // ---
    // Handler per FabrickApiBusinessException
    // Gestisce errori di business da Fabrick (es. validazione input, come IBAN/CF non validi, ecc.),
    // anche se Fabrick li ha restituiti con HTTP 200 ma con status "KO" interno,
    // o con HTTP 400 e payload di errore.
    // ---
    @ExceptionHandler(FabrickApiBusinessException.class)
    public ResponseEntity<TransactionManagerApiResponse<?>> handleFabrickApiBusinessException(FabrickApiBusinessException ex) {
        // ex.getMessage() contiene il messaggio dettagliato costruito nel FabrickFeignErrorDecoder
        // (es. "Fabrick input validation error: Codice fiscale ordinante formalmente non valido")
        log.warn("Fabrick business error: '{}'. Details: {}", ex.getMessage(), ex.getErrors(), ex);

        // Il messaggio per il client sarà proprio ex.getMessage()
        // Il codice interno sarà ex.getErrorCode().getCode()
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new TransactionManagerApiResponse<>(
                        null,
                        getTransactionManagerErrors(ex)
                )
        );
    }

    // ---
    // Handler per MethodArgumentNotValidException (Errori di validazione @Valid nei DTO di richiesta)
    // ---
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

    // ---
    // Handler per ConstraintViolationException (Errori di validazione @Validated su parametri/variabili di path)
    // ---
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

    // ---
    // Handler catch-all per tutte le altre eccezioni non gestite esplicitamente.
    // ---
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

    private static List<TransactionManagerApiResponse.TransactionManagerError> getTransactionManagerErrors(FabrickApiBusinessException ex) {
        return ex.getErrorCodes().stream().map(
                errorcode -> new TransactionManagerApiResponse.TransactionManagerError(
                        errorcode.getCode(),
                        errorcode.getDefaultMessage()
                )
        ).collect(Collectors.toList());
    }
}