package org.example.fooddeliverysystem.exception;

public enum ErrorCode {
    // Resource errors
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "The requested resource was not found"),
    
    // Validation errors
    VALIDATION_FAILED("VALIDATION_FAILED", "Request validation failed"),
    INVALID_INPUT("INVALID_INPUT", "Invalid input provided"),
    
    // Business logic errors
    BUSINESS_RULE_VIOLATION("BUSINESS_RULE_VIOLATION", "Business rule violation"),
    ACCOUNT_DISABLED("ACCOUNT_DISABLED", "Account is disabled"),
    
    // Authentication/Authorization errors
    UNAUTHORIZED("UNAUTHORIZED", "Unauthorized access"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Invalid credentials"),
    
    // Conflict errors
    RESOURCE_CONFLICT("RESOURCE_CONFLICT", "Resource already exists"),
    DUPLICATE_EMAIL("DUPLICATE_EMAIL", "Email already exists"),
    DUPLICATE_PHONE("DUPLICATE_PHONE", "Phone number already exists"),
    
    // Server errors
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "An unexpected error occurred");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}
