package org.example.fooddeliverysystem.exception;

public class UnauthorizedException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    public UnauthorizedException(String message) {
        super(message);
        this.errorCode = ErrorCode.UNAUTHORIZED;
    }
    
    public UnauthorizedException() {
        super("Unauthorized access");
        this.errorCode = ErrorCode.UNAUTHORIZED;
    }
    
    public UnauthorizedException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
