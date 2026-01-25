package org.example.fooddeliverysystem.dto.event;

import java.time.LocalDateTime;
import java.util.Map;

public class AnalyticsEvent {
    
    private String eventType;
    private String entityType;
    private String entityId;
    private Map<String, Object> metrics;
    private LocalDateTime timestamp;
    
    public AnalyticsEvent() {
        this.timestamp = LocalDateTime.now();
    }
    
    public AnalyticsEvent(String eventType, String entityType, String entityId, 
                         Map<String, Object> metrics) {
        this();
        this.eventType = eventType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.metrics = metrics;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    public String getEntityId() {
        return entityId;
    }
    
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
    
    public Map<String, Object> getMetrics() {
        return metrics;
    }
    
    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
