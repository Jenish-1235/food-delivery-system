package org.example.fooddeliverysystem.controller;

import jakarta.validation.Valid;
import org.example.fooddeliverysystem.dto.restaurant.RestaurantRequest;
import org.example.fooddeliverysystem.dto.restaurant.RestaurantResponse;
import org.example.fooddeliverysystem.model.User;
import org.example.fooddeliverysystem.service.RestaurantService;
import org.example.fooddeliverysystem.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    
    private final RestaurantService restaurantService;
    private final UserService userService;
    
    public RestaurantController(RestaurantService restaurantService, UserService userService) {
        this.restaurantService = restaurantService;
        this.userService = userService;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT') or hasRole('ADMIN')")
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody RestaurantRequest request) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        RestaurantResponse response = restaurantService.create(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable String id) {
        RestaurantResponse response = restaurantService.findById(id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT') or hasRole('ADMIN')")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable String id, 
            @Valid @RequestBody RestaurantRequest request) {
        RestaurantResponse response = restaurantService.update(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getRestaurants(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state) {
        List<RestaurantResponse> restaurants;
        
        if (city != null && state != null) {
            restaurants = restaurantService.findByCityAndState(city, state);
        } else if (city != null) {
            restaurants = restaurantService.findByCity(city);
        } else {
            restaurants = List.of();
        }
        
        return ResponseEntity.ok(restaurants);
    }
    
    @GetMapping("/my-restaurant")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<RestaurantResponse> getMyRestaurant() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        RestaurantResponse response = restaurantService.findByUser(currentUser);
        return ResponseEntity.ok(response);
    }
}
