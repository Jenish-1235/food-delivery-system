package org.example.fooddeliverysystem.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fooddeliverysystem.config.KafkaTopics;
import org.example.fooddeliverysystem.dto.event.DeliveryEvent;
import org.example.fooddeliverysystem.service.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DeliveryAnalyticsConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(DeliveryAnalyticsConsumer.class);
    
    private final ObjectMapper objectMapper;
    private final MetricsService metricsService;
    
    public DeliveryAnalyticsConsumer(ObjectMapper objectMapper, MetricsService metricsService) {
        this.objectMapper = objectMapper;
        this.metricsService = metricsService;
    }
    
    @KafkaListener(topics = KafkaTopics.DELIVERY_EVENTS, groupId = "delivery-analytics-group")
    public void consumeDeliveryEvent(String message) {
        try {
            DeliveryEvent event = objectMapper.readValue(message, DeliveryEvent.class);
            logger.info("Processing delivery event: {} for order: {}", event.getEventType(), event.getOrderId());
            
            // Update metrics based on delivery status
            if ("DELIVERY_COMPLETED".equals(event.getEventType())) {
                metricsService.incrementDeliveriesCompleted();
            }
        } catch (Exception e) {
            logger.error("Error processing delivery event: {}", e.getMessage(), e);
            metricsService.incrementErrors();
        }
    }
}
