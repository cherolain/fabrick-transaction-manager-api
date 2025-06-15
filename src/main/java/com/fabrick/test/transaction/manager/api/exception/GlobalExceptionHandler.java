package com.fabrick.test.transaction.manager.api.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // La tua definizione esistente di ErrorResponse
    public record ErrorResponse(String code, String description) {}

    // ---
    // Handler per FabrickApiException
    // Gestisce errori di comunicazione HTTP (es. 400, 401, 403, 404, 500, 503) direttamente da Fabrick,
    // come tradotti dal FabrickFeignErrorDecoder.
    // ---
    @ExceptionHandler(FabrickApiException.class)
    public ResponseEntity<ErrorResponse> handleFabrickApiException(FabrickApiException ex) {
        // ex.getMessage() contiene il messaggio dettagliato costruito nel FabrickFeignErrorDecoder
        // (es. "Fabrick API returned an error: Unauthorized. Details: Credenziali API non valide o scadute")
        log.error("Fabrick API HTTP error: {}. Status: {}. ErrorCode: {}", ex.getMessage(), ex.getHttpStatus(), ex.getErrorCode(), ex);

        // Il messaggio per il client sarà proprio ex.getMessage()
        // Il codice interno sarà ex.getErrorCode().getCode()
        return buildErrorResponseEntity(
                ex.getErrorCode() != null ? ex.getErrorCode().getCode() : ErrorCode.EXTERNAL_API_FAILURE.getCode(),
                ex.getMessage(),
                ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // ---
    // Handler per FabrickApiBusinessException
    // Gestisce errori di business da Fabrick (es. validazione input, come IBAN/CF non validi, ecc.),
    // anche se Fabrick li ha restituiti con HTTP 200 ma con status "KO" interno,
    // o con HTTP 400 e payload di errore.
    // ---
    @ExceptionHandler(FabrickApiBusinessException.class)
    public ResponseEntity<ErrorResponse> handleFabrickApiBusinessException(FabrickApiBusinessException ex) {
        // ex.getMessage() contiene il messaggio dettagliato costruito nel FabrickFeignErrorDecoder
        // (es. "Fabrick input validation error: Codice fiscale ordinante formalmente non valido")
        log.warn("Fabrick business error: '{}'. Details: {}", ex.getMessage(), ex.getErrors(), ex);

        // Il messaggio per il client sarà proprio ex.getMessage()
        // Il codice interno sarà ex.getErrorCode().getCode()
        return buildErrorResponseEntity(
                ex.getErrorCode() != null ? ex.getErrorCode().getCode() : ErrorCode.BUSINESS_ERROR.getCode(),
                ex.getMessage(), // **Qui propaghiamo il messaggio parlante**
                HttpStatus.BAD_REQUEST // Per errori di business/validazione, restituiamo 400
        );
    }

    // ---
    // Handler per InternalApplicationException
    // Gestisce errori interni inaspettati della tua applicazione.
    // ---
    @ExceptionHandler(InternalApplicationException.class)
    public ResponseEntity<ErrorResponse> handleInternalApplicationException(InternalApplicationException ex) {
        log.error("Internal application error occurred: {}", ex.getMessage(), ex);

        // Per errori interni, di solito si preferisce un messaggio generico per il client
        // per non esporre dettagli di implementazione o stack trace.
        String clientMessage = ex.getErrorCode() != null ? ex.getErrorCode().getDefaultMessage() : ErrorCode.UNEXPECTED_ERROR.getDefaultMessage();

        return buildErrorResponseEntity(
                ex.getErrorCode() != null ? ex.getErrorCode().getCode() : ErrorCode.UNEXPECTED_ERROR.getCode(),
                clientMessage, // Messaggio generico per il client
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // ---
    // Handler per MethodArgumentNotValidException (Errori di validazione @Valid nei DTO di richiesta)
    // ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
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

        return buildErrorResponseEntity(
                ErrorCode.VALIDATION_ERROR.getCode(),
                clientMessage,
                HttpStatus.BAD_REQUEST
        );
    }

    // ---
    // Handler per ConstraintViolationException (Errori di validazione @Validated su parametri/variabili di path)
    // ---
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String constraintDetails = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));

        log.warn("Constraint violation for request parameters: {}", constraintDetails, ex);

        String clientMessage = ErrorCode.INVALID_PATH_VARIABLE.getDefaultMessage() + " " + constraintDetails;

        return buildErrorResponseEntity(
                ErrorCode.INVALID_PATH_VARIABLE.getCode(),
                clientMessage,
                HttpStatus.BAD_REQUEST
        );
    }

    // ---
    // Handler per NoResourceFoundException (Spring Web - risorsa non trovata o metodo HTTP non supportato)
    // ---
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        log.warn("Resource not found or unsupported method: {}", ex.getMessage(), ex);
        return buildErrorResponseEntity(
                ErrorCode.NOT_FOUND.getCode(),
                ErrorCode.NOT_FOUND.getDefaultMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    // ---
    // Handler catch-all per tutte le altre eccezioni non gestite esplicitamente.
    // ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("An unhandled exception occurred: {}", ex.getMessage(), ex);
        return buildErrorResponseEntity(
                ErrorCode.UNEXPECTED_ERROR.getCode(), // Cambiato da GENERIC_ERROR
                ErrorCode.UNEXPECTED_ERROR.getDefaultMessage(), // Cambiato da GENERIC_ERROR
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // ---
    // Metodo di utilità per costruire in modo consistente la ResponseEntity.
    // ---
    private ResponseEntity<ErrorResponse> buildErrorResponseEntity(String code, String description, HttpStatus status) {
        return new ResponseEntity<>(new ErrorResponse(code, description), status);
    }
}