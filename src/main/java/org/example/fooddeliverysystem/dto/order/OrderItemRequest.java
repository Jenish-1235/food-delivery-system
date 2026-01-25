package org.example.fooddeliverysystem.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OrderItemRequest {
    
    @NotBlank(message = "Food item ID is required")
    private String foodItemId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    public OrderItemRequest() {}
    
    public String getFoodItemId() {
        return foodItemId;
    }
    
    public void setFoodItemId(String foodItemId) {
        this.foodItemId = foodItemId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
