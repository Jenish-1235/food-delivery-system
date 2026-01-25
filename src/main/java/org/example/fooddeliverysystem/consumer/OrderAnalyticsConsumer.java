package org.example.fooddeliverysystem.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fooddeliverysystem.config.KafkaTopics;
import org.example.fooddeliverysystem.dto.event.OrderEvent;
import org.example.fooddeliverysystem.service.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderAnalyticsConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderAnalyticsConsumer.class);
    
    private final ObjectMapper objectMapper;
    private final MetricsService metricsService;
    
    public OrderAnalyticsConsumer(ObjectMapper objectMapper, MetricsService metricsService) {
        this.objectMapper = objectMapper;
        this.metricsService = metricsService;
    }
    
    @KafkaListener(topics = KafkaTopics.ORDER_EVENTS, groupId = "order-analytics-group")
    public void consumeOrderEvent(String message) {
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            logger.info("Processing order event: {} for order: {}", event.getEventType(), event.getOrderId());
            
            // Update metrics based on event type
            switch (event.getEventType()) {
                case "ORDER_CREATED":
                    metricsService.incrementOrdersCreated();
                    break;
                case "ORDER_DELIVERED":
                    metricsService.incrementOrdersDelivered();
                    break;
                case "ORDER_CANCELLED":
                    metricsService.incrementOrdersCancelled();
                    break;
                default:
                    logger.debug("No specific metric update for event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            logger.error("Error processing order event: {}", e.getMessage(), e);
            metricsService.incrementErrors();
        }
    }
}
