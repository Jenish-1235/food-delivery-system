package org.example.fooddeliverysystem.exception;

public class ErrorMessage {
    
    // Resource messages
    public static final String USER_NOT_FOUND = "User not found";
    public static final String RESTAURANT_NOT_FOUND = "Restaurant not found";
    public static final String FOOD_ITEM_NOT_FOUND = "Food item not found";
    public static final String ORDER_NOT_FOUND = "Order not found";
    public static final String DRIVER_NOT_FOUND = "Driver not found";
    
    // Validation messages
    public static final String INVALID_EMAIL = "Invalid email format";
    public static final String INVALID_PHONE = "Invalid phone number format";
    public static final String INVALID_PASSWORD = "Password does not meet requirements";
    
    // Business logic messages
    public static final String RESTAURANT_CLOSED = "Restaurant is currently closed";
    public static final String FOOD_ITEM_UNAVAILABLE = "Food item is not available";
    public static final String INVALID_ORDER_STATUS = "Invalid order status transition";
    public static final String DRIVER_NOT_AVAILABLE = "Driver is not available";
    
    // Conflict messages
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String PHONE_ALREADY_EXISTS = "Phone number already exists";
    
    // Authorization messages
    public static final String INVALID_CREDENTIALS = "Invalid email/phone or password";
    public static final String ACCOUNT_DISABLED = "Account is disabled";
    public static final String ACCESS_DENIED = "Access denied";
    
    private ErrorMessage() {
        // Utility class
    }
}
