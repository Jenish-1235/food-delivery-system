package org.example.fooddeliverysystem.exception;

public class BusinessException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    public BusinessException(String message) {
        super(message);
        this.errorCode = ErrorCode.BUSINESS_RULE_VIOLATION;
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.BUSINESS_RULE_VIOLATION;
    }
    
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
