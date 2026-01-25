package org.example.fooddeliverysystem.exception;

public class ConflictException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    public ConflictException(String message) {
        super(message);
        this.errorCode = ErrorCode.RESOURCE_CONFLICT;
    }
    
    public ConflictException(String resource, String identifier) {
        super(String.format("%s already exists with identifier: %s", resource, identifier));
        this.errorCode = ErrorCode.RESOURCE_CONFLICT;
    }
    
    public ConflictException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
