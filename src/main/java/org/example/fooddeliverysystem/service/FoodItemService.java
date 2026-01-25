package org.example.fooddeliverysystem.service;

import org.example.fooddeliverysystem.dto.fooditem.FoodItemRequest;
import org.example.fooddeliverysystem.dto.fooditem.FoodItemResponse;
import org.example.fooddeliverysystem.exception.BusinessException;
import org.example.fooddeliverysystem.exception.ResourceNotFoundException;
import org.example.fooddeliverysystem.model.FoodItem;
import org.example.fooddeliverysystem.model.Restaurant;
import org.example.fooddeliverysystem.repository.FoodItemRepository;
import org.example.fooddeliverysystem.repository.RestaurantRepository;
import org.example.fooddeliverysystem.util.CacheKeys;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class FoodItemService {
    
    private final FoodItemRepository foodItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final CacheService cacheService;
    
    public FoodItemService(FoodItemRepository foodItemRepository, 
                          RestaurantRepository restaurantRepository,
                          CacheService cacheService) {
        this.foodItemRepository = foodItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.cacheService = cacheService;
    }
    
    @Transactional
    public FoodItemResponse create(String restaurantId, FoodItemRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant", restaurantId));
        
        FoodItem foodItem = new FoodItem(
            restaurant,
            request.getName(),
            request.getDescription(),
            request.getImgUrl(),
            request.getPrice(),
            request.isAvailable()
        );
        
        foodItem = foodItemRepository.save(foodItem);
        FoodItemResponse response = mapToResponse(foodItem);
        
        // Invalidate restaurant menu cache
        cacheService.delete(CacheKeys.restaurantMenuKey(restaurantId));
        
        return response;
    }
    
    @Transactional
    public FoodItemResponse update(String id, FoodItemRequest request) {
        FoodItem foodItem = foodItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("FoodItem", id));
        
        if (foodItem.isDeleted()) {
            throw new BusinessException("Cannot update deleted food item");
        }
        
        foodItem.setName(request.getName());
        foodItem.setDescription(request.getDescription());
        foodItem.setImgUrl(request.getImgUrl());
        foodItem.setPrice(request.getPrice());
        foodItem.setAvailable(request.isAvailable());
        
        foodItem = foodItemRepository.save(foodItem);
        FoodItemResponse response = mapToResponse(foodItem);
        
        // Invalidate restaurant menu cache
        cacheService.delete(CacheKeys.restaurantMenuKey(foodItem.getRestaurant().getId()));
        
        return response;
    }
    
    @Transactional
    public void delete(String id) {
        FoodItem foodItem = foodItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("FoodItem", id));
        
        foodItem.setDeleted(true);
        foodItem.setAvailable(false);
        foodItemRepository.save(foodItem);
        
        // Invalidate restaurant menu cache
        cacheService.delete(CacheKeys.restaurantMenuKey(foodItem.getRestaurant().getId()));
    }
    
    public FoodItemResponse findById(String id) {
        FoodItem foodItem = foodItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("FoodItem", id));
        
        if (foodItem.isDeleted()) {
            throw new ResourceNotFoundException("FoodItem", id);
        }
        
        return mapToResponse(foodItem);
    }
    
    public List<FoodItemResponse> findByRestaurant(Restaurant restaurant) {
        String cacheKey = CacheKeys.restaurantMenuKey(restaurant.getId());
        
        // Try cache first
        @SuppressWarnings("unchecked")
        List<FoodItemResponse> cached = (List<FoodItemResponse>) cacheService.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        // Cache miss - fetch from database
        List<FoodItemResponse> foodItems = foodItemRepository.findByRestaurantAndIsDeletedFalse(restaurant).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
        
        // Cache the result
        cacheService.setWithExpiry(cacheKey, foodItems, CacheKeys.RESTAURANT_MENU_TTL, TimeUnit.SECONDS);
        
        return foodItems;
    }
    
    public List<FoodItemResponse> findAvailableByRestaurant(Restaurant restaurant) {
        return foodItemRepository.findByRestaurantAndIsAvailableTrueAndIsDeletedFalse(restaurant).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    private FoodItemResponse mapToResponse(FoodItem foodItem) {
        FoodItemResponse response = new FoodItemResponse();
        response.setId(foodItem.getId());
        response.setRestaurantId(foodItem.getRestaurant().getId());
        response.setName(foodItem.getName());
        response.setDescription(foodItem.getDescription());
        response.setImgUrl(foodItem.getImgUrl());
        response.setPrice(foodItem.getPrice());
        response.setAvailable(foodItem.isAvailable());
        response.setDeleted(foodItem.isDeleted());
        response.setMetadata(foodItem.getMetadata());
        response.setCreatedAt(foodItem.getCreatedAt());
        response.setUpdatedAt(foodItem.getUpdatedAt());
        return response;
    }
}
