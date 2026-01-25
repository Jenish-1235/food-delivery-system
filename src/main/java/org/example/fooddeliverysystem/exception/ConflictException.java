package org.example.fooddeliverysystem.exception;

public class ConflictException extends RuntimeException {
    
    public ConflictException(String message) {
        super(message);
    }
    
    public ConflictException(String resource, String identifier) {
        super(String.format("%s already exists with identifier: %s", resource, identifier));
    }
}
