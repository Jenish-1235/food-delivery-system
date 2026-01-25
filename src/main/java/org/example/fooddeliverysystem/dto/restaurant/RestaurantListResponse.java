package org.example.fooddeliverysystem.dto.restaurant;

import java.util.List;

public class RestaurantListResponse {
    
    private List<RestaurantResponse> restaurants;
    private int total;
    private int page;
    private int size;
    
    public RestaurantListResponse() {}
    
    public RestaurantListResponse(List<RestaurantResponse> restaurants, int total, int page, int size) {
        this.restaurants = restaurants;
        this.total = total;
        this.page = page;
        this.size = size;
    }
    
    public List<RestaurantResponse> getRestaurants() {
        return restaurants;
    }
    
    public void setRestaurants(List<RestaurantResponse> restaurants) {
        this.restaurants = restaurants;
    }
    
    public int getTotal() {
        return total;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
}
