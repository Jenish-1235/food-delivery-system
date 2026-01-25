package org.example.fooddeliverysystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fooddeliverysystem.config.KafkaTopics;
import org.example.fooddeliverysystem.dto.event.AnalyticsEvent;
import org.example.fooddeliverysystem.dto.event.DeliveryEvent;
import org.example.fooddeliverysystem.dto.event.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaEventProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaEventProducer.class);
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    public KafkaEventProducer(KafkaTemplate<String, String> kafkaTemplate, 
                             ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    
    public void publishOrderEvent(OrderEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(KafkaTopics.ORDER_EVENTS, event.getOrderId(), message);
            logger.info("Published order event: {} for order: {}", event.getEventType(), event.getOrderId());
        } catch (JsonProcessingException e) {
            logger.error("Error serializing order event: {}", e.getMessage(), e);
        }
    }
    
    public void publishDeliveryEvent(DeliveryEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(KafkaTopics.DELIVERY_EVENTS, event.getOrderId(), message);
            logger.info("Published delivery event: {} for order: {}", event.getEventType(), event.getOrderId());
        } catch (JsonProcessingException e) {
            logger.error("Error serializing delivery event: {}", e.getMessage(), e);
        }
    }
    
    public void publishAnalyticsEvent(AnalyticsEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(KafkaTopics.ANALYTICS_EVENTS, event.getEntityId(), message);
            logger.info("Published analytics event: {} for entity: {}", event.getEventType(), event.getEntityId());
        } catch (JsonProcessingException e) {
            logger.error("Error serializing analytics event: {}", e.getMessage(), e);
        }
    }
}
