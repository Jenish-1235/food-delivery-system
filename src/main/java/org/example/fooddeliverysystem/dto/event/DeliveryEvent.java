package org.example.fooddeliverysystem.dto.event;

import java.time.LocalDateTime;

public class DeliveryEvent {
    
    private String eventType;
    private String orderId;
    private String driverId;
    private Double latitude;
    private Double longitude;
    private String status;
    private LocalDateTime timestamp;
    
    public DeliveryEvent() {
        this.timestamp = LocalDateTime.now();
    }
    
    public DeliveryEvent(String eventType, String orderId, String driverId, 
                        Double latitude, Double longitude, String status) {
        this();
        this.eventType = eventType;
        this.orderId = orderId;
        this.driverId = driverId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
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
    
    public String getDriverId() {
        return driverId;
    }
    
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
