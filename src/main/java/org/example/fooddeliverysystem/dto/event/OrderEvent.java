package org.example.fooddeliverysystem.dto.event;

import java.time.LocalDateTime;

public class OrderEvent {
    
    private String eventType;
    private String orderId;
    private String orderNumber;
    private String userId;
    private String restaurantId;
    private String driverId;
    private String orderStatus;
    private Double amount;
    private LocalDateTime timestamp;
    
    public OrderEvent() {
        this.timestamp = LocalDateTime.now();
    }
    
    public OrderEvent(String eventType, String orderId, String orderNumber, String userId, 
                     String restaurantId, String orderStatus, Double amount) {
        this();
        this.eventType = eventType;
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.orderStatus = orderStatus;
        this.amount = amount;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getRestaurantId() {
        return restaurantId;
    }
    
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    public String getDriverId() {
        return driverId;
    }
    
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
    
    public String getOrderStatus() {
        return orderStatus;
    }
    
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
