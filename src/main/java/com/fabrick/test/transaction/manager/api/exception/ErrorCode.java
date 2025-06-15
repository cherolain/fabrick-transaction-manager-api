package com.fabrick.test.transaction.manager.api.exception;

public enum ErrorCode {
    VALIDATION_ERROR("E003", "Data validation error. Please check your input."),
    INVALID_PATH_VARIABLE("E004", "Invalid path parameter. Please check the URL."),
    NOT_FOUND("E007", "The requested resource was not found."),
    UNAUTHORIZED("E005", "Unauthorized access. Please provide valid credentials."),
    FORBIDDEN("E006", "Access denied. You do not have permission to perform this action."),
    SERVICE_UNAVAILABLE("E008", "The service is temporarily unavailable. Please try again later."),
    METHOD_NOT_ALLOWED("E009", "The requested method is not allowed for this resource."),
    BAD_REQUEST("E002", "The request was invalid. Please check your input and try again."),
    INTERNAL_SERVER_ERROR("E012", "An internal server error occurred. Please try again later."),
    UNEXPECTED_ERROR("E013", "An unexpected error occurred. Please try again later."),
    EXTERNAL_API_FAILURE("E001", "Communication error with the external service. Please try again later.");

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