package org.example.fooddeliverysystem.dto.order;

import org.example.fooddeliverysystem.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;

public class OrderStatusUpdateRequest {
    
    @NotNull(message = "Order status is required")
    private OrderStatus orderStatus;
    
    public OrderStatusUpdateRequest() {}
    
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
