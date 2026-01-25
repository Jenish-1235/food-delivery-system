package org.example.fooddeliverysystem.dto.fooditem;

import java.util.List;

public class FoodItemListResponse {
    
    private List<FoodItemResponse> foodItems;
    private int total;
    
    public FoodItemListResponse() {}
    
    public FoodItemListResponse(List<FoodItemResponse> foodItems, int total) {
        this.foodItems = foodItems;
        this.total = total;
    }
    
    public List<FoodItemResponse> getFoodItems() {
        return foodItems;
    }
    
    public void setFoodItems(List<FoodItemResponse> foodItems) {
        this.foodItems = foodItems;
    }
    
    public int getTotal() {
        return total;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }
}
