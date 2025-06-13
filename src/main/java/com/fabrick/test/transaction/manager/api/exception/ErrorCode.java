package com.fabrick.test.transaction.manager.api.exception;

public enum ErrorCode {
    EXTERNAL_API_FAILURE("E001", "Communication error with the fabrick service."),
    BUSINESS_ERROR("E002", "Business error from service."),
    VALIDATION_ERROR("E003", "Data validation error."),
    INVALID_PATH_VARIABLE("E004", "Invalid path parameter."),
    UNAUTHORIZED("E005", "Unauthorized access."),
    FORBIDDEN("E006", "Access denied."),
    NOT_FOUND("E007", "Resource not found."),
    SERVICE_UNAVAILABLE("E008", "Service unavailable."),
    GENERIC_ERROR("E999", "Unexpected error. Please contact support.");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}