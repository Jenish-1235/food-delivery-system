package org.example.fooddeliverysystem.config;

public class KafkaTopics {
    
    public static final String ORDER_EVENTS = "order-events";
    public static final String DELIVERY_EVENTS = "delivery-events";
    public static final String ANALYTICS_EVENTS = "analytics-events";
    public static final String RESTAURANT_EVENTS = "restaurant-events";
    public static final String DRIVER_EVENTS = "driver-events";
    
    private KafkaTopics() {
        // Utility class
    }
}
