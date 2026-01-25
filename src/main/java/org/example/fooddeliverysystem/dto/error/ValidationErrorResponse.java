package org.example.fooddeliverysystem.dto.error;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ValidationErrorResponse extends ErrorResponse {
    
    private List<FieldError> fieldErrors;
    
    public ValidationErrorResponse() {
        super();
        this.fieldErrors = new ArrayList<>();
    }
    
    public ValidationErrorResponse(int status, String error, String message, String path) {
        super(status, error, message, path);
        this.fieldErrors = new ArrayList<>();
    }
    
    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }
    
    public void setFieldErrors(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
    
    public void addFieldError(FieldError fieldError) {
        this.fieldErrors.add(fieldError);
    }
}
