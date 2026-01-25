package org.example.fooddeliverysystem.controller;

import jakarta.validation.Valid;
import org.example.fooddeliverysystem.dto.fooditem.FoodItemRequest;
import org.example.fooddeliverysystem.dto.fooditem.FoodItemResponse;
import org.example.fooddeliverysystem.model.Restaurant;
import org.example.fooddeliverysystem.model.User;
import org.example.fooddeliverysystem.repository.RestaurantRepository;
import org.example.fooddeliverysystem.service.FoodItemService;
import org.example.fooddeliverysystem.service.RestaurantService;
import org.example.fooddeliverysystem.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food-items")
public class FoodItemController {
    
    private final FoodItemService foodItemService;
    private final RestaurantService restaurantService;
    private final RestaurantRepository restaurantRepository;
    private final UserService userService;
    
    public FoodItemController(FoodItemService foodItemService,
                             RestaurantService restaurantService,
                             RestaurantRepository restaurantRepository,
                             UserService userService) {
        this.foodItemService = foodItemService;
        this.restaurantService = restaurantService;
        this.restaurantRepository = restaurantRepository;
        this.userService = userService;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT') or hasRole('ADMIN')")
    public ResponseEntity<FoodItemResponse> createFoodItem(
            @RequestParam String restaurantId,
            @Valid @RequestBody FoodItemRequest request) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Verify restaurant ownership
        org.example.fooddeliverysystem.dto.restaurant.RestaurantResponse restaurantResponse = 
            restaurantService.findById(restaurantId);
        if (!restaurantResponse.getUserId().equals(currentUser.getId()) && 
            !currentUser.getRole().name().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        FoodItemResponse response = foodItemService.create(restaurantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FoodItemResponse> getFoodItem(@PathVariable String id) {
        FoodItemResponse response = foodItemService.findById(id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT') or hasRole('ADMIN')")
    public ResponseEntity<FoodItemResponse> updateFoodItem(
            @PathVariable String id,
            @Valid @RequestBody FoodItemRequest request) {
        FoodItemResponse response = foodItemService.update(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable String id) {
        foodItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<List<FoodItemResponse>> getFoodItemsByRestaurant(
            @PathVariable String restaurantId,
            @RequestParam(required = false, defaultValue = "false") boolean availableOnly) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new org.example.fooddeliverysystem.exception.ResourceNotFoundException("Restaurant", restaurantId));
        List<FoodItemResponse> foodItems;
        
        if (availableOnly) {
            foodItems = foodItemService.findAvailableByRestaurant(restaurant);
        } else {
            foodItems = foodItemService.findByRestaurant(restaurant);
        }
        
        return ResponseEntity.ok(foodItems);
    }
}
