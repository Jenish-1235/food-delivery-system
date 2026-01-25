package org.example.fooddeliverysystem.exception;

public class ValidationException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    public ValidationException(String message) {
        super(message);
        this.errorCode = ErrorCode.VALIDATION_FAILED;
    }
    
    public ValidationException(String field, String message) {
        super(String.format("Validation failed for field '%s': %s", field, message));
        this.errorCode = ErrorCode.VALIDATION_FAILED;
    }
    
    public ValidationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
