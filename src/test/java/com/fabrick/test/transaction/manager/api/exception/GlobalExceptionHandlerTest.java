package com.fabrick.test.transaction.manager.api.exception;

import com.fabrick.test.transaction.manager.api.dto.response.FabrickApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    // A dummy method for MethodParameter in MethodArgumentNotValidException test
    public void dummyMethodForMethodArgumentNotValidExceptionTest() {}

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    // --- Tests for FabrickApiException ---
    static Stream<org.junit.jupiter.params.provider.Arguments> fabrickApiExceptionProvider() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of("Internal Not Found message", HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND),
                org.junit.jupiter.params.provider.Arguments.of("Internal Bad Request message", HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST),
                org.junit.jupiter.params.provider.Arguments.of("Internal Unauthorized message", HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED),
                org.junit.jupiter.params.provider.Arguments.of("Internal Internal Server Error message", HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.EXTERNAL_API_FAILURE)
        );
    }

    @ParameterizedTest
    @MethodSource("fabrickApiExceptionProvider")
    void handleFabrickApiException_parametrized(String internalMessage, HttpStatus status, ErrorCode errorCode) {
        // Il messaggio qui è quello che verrebbe passato al costruttore di FabrickApiException
        FabrickApiException ex = new FabrickApiException(internalMessage, status, errorCode);
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleFabrickApiException(ex);

        assertNotNull(response);
        assertEquals(status, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorCode.getCode(), response.getBody().code());
        assertEquals(internalMessage, response.getBody().description());
    }

    // --- Tests for FabrickApiBusinessException ---
    @Test
    void handleFabrickApiBusinessException_shouldReturnBadRequest() {
        // Uso FabrickApiResponse.FabrickError come nel tuo codice originale
        FabrickApiResponse.FabrickError fabrickError = new FabrickApiResponse.FabrickError("BP049", "Fondi insufficienti per l'operazione.");
        List<FabrickApiResponse.FabrickError> fabrickErrors = Collections.singletonList(fabrickError);

        // L'ErrorCode ora corrisponde a quello della tua enum
        ErrorCode testErrorCode = ErrorCode.FAB_INSUFFICIENT_FUNDS;

        // Il messaggio interno dell'eccezione (per i log)
        String internalMessage = "Fabrick business error due to BP049.";

        FabrickApiBusinessException ex = new FabrickApiBusinessException(internalMessage, fabrickErrors, testErrorCode);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> responseEntity =
                globalExceptionHandler.handleFabrickApiBusinessException(ex);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(testErrorCode.getCode(), responseEntity.getBody().code());
        assertEquals(internalMessage, responseEntity.getBody().description());
    }

    // --- Tests for InternalApplicationException ---
    @Test
    void handleInternalApplicationException_shouldReturnInternalServerError() {
        // Uso UNEXPECTED_ERROR o INTERNAL_SERVER_ERROR a seconda della tua preferenza come default
        // Ho scelto UNEXPECTED_ERROR per il test, ma INTERNAL_SERVER_ERROR è il fallback nel handler se null.
        ErrorCode testErrorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        String internalMessage = "Errore di connessione al database.";
        InternalApplicationException ex = new InternalApplicationException(internalMessage, new RuntimeException("DB down"), testErrorCode);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> responseEntity =
                globalExceptionHandler.handleInternalApplicationException(ex);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(testErrorCode.getCode(), responseEntity.getBody().code());
        assertEquals(testErrorCode.getDefaultMessage(), responseEntity.getBody().description());
    }

    // --- Tests for MethodArgumentNotValidException ---
    @Test
    void handleMethodArgumentNotValidException_shouldReturnValidationDetails() throws NoSuchMethodException {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "Il campo non può essere vuoto");
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

        Method method = this.getClass().getDeclaredMethod("dummyMethodForMethodArgumentNotValidExceptionTest");
        MethodParameter methodParameter = new MethodParameter(method, -1);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> responseEntity =
                globalExceptionHandler.handleValidationExceptions(ex);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(ErrorCode.VALIDATION_ERROR.getCode(), responseEntity.getBody().code());
        String expectedDescription = ErrorCode.VALIDATION_ERROR.getDefaultMessage() + " fieldName: Il campo non può essere vuoto";
        assertEquals(expectedDescription, responseEntity.getBody().description());
    }

    // --- Tests for ConstraintViolationException ---
    @Test
    void handleConstraintViolationException_shouldReturnConstraintDetails() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("paramName");
        when(violation.getMessage()).thenReturn("non deve essere nullo");

        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(violation);

        ConstraintViolationException ex = new ConstraintViolationException(violations);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> responseEntity =
                globalExceptionHandler.handleConstraintViolation(ex);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(ErrorCode.INVALID_PATH_VARIABLE.getCode(), responseEntity.getBody().code());
        String expectedDescription = ErrorCode.INVALID_PATH_VARIABLE.getDefaultMessage() + " paramName: non deve essere nullo";
        assertEquals(expectedDescription, responseEntity.getBody().description());
    }

    // --- Tests for NoResourceFoundException ---
    @Test
    void handleNoResourceFoundException_shouldReturnNotFound() {
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "/inesistente");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> responseEntity =
                globalExceptionHandler.handleNoResourceFound(ex);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(ErrorCode.NOT_FOUND.getCode(), responseEntity.getBody().code());
        assertEquals(ErrorCode.NOT_FOUND.getDefaultMessage(), responseEntity.getBody().description());
    }

    // --- Tests for Generic Exception ---
    @Test
    void handleGenericException_shouldReturnInternalServerError() {
        Exception ex = new Exception("È successo qualcosa di inatteso!");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> responseEntity =
                globalExceptionHandler.handleGenericException(ex);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        // Ora usa GENERIC_ERROR come da tua enum
        assertEquals(ErrorCode.UNEXPECTED_ERROR.getCode(), responseEntity.getBody().code());
        assertEquals(ErrorCode.UNEXPECTED_ERROR.getDefaultMessage(), responseEntity.getBody().description());
    }
}