package org.example.fooddeliverysystem.service;

import org.example.fooddeliverysystem.dto.restaurant.RestaurantRequest;
import org.example.fooddeliverysystem.dto.restaurant.RestaurantResponse;
import org.example.fooddeliverysystem.exception.ResourceNotFoundException;
import org.example.fooddeliverysystem.model.Restaurant;
import org.example.fooddeliverysystem.model.User;
import org.example.fooddeliverysystem.repository.RestaurantRepository;
import org.example.fooddeliverysystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {
    
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    
    public RestaurantService(RestaurantRepository restaurantRepository, 
                            UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }
    
    @Transactional
    public RestaurantResponse create(String userId, RestaurantRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        Restaurant restaurant = new Restaurant(
            user,
            request.getRestaurantName(),
            request.getCity(),
            request.getState(),
            request.getZipCode(),
            request.getLatitude(),
            request.getLongitude(),
            request.getOpeningTime(),
            request.getClosingTime(),
            request.getImgUrl(),
            request.isOpen(),
            null
        );
        
        restaurant = restaurantRepository.save(restaurant);
        return mapToResponse(restaurant);
    }
    
    @Transactional
    public RestaurantResponse update(String id, RestaurantRequest request) {
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant", id));
        
        restaurant.setRestaurantName(request.getRestaurantName());
        restaurant.setCity(request.getCity());
        restaurant.setState(request.getState());
        restaurant.setZipCode(request.getZipCode());
        restaurant.setLatitude(request.getLatitude());
        restaurant.setLongitude(request.getLongitude());
        restaurant.setOpeningTime(request.getOpeningTime());
        restaurant.setClosingTime(request.getClosingTime());
        restaurant.setImgUrl(request.getImgUrl());
        restaurant.setOpen(request.isOpen());
        
        restaurant = restaurantRepository.save(restaurant);
        return mapToResponse(restaurant);
    }
    
    public RestaurantResponse findById(String id) {
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant", id));
        return mapToResponse(restaurant);
    }
    
    public RestaurantResponse findByUser(User user) {
        Restaurant restaurant = restaurantRepository.findByUser(user)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "user: " + user.getId()));
        return mapToResponse(restaurant);
    }
    
    public List<RestaurantResponse> findByCity(String city) {
        return restaurantRepository.findByCity(city).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<RestaurantResponse> findByCityAndState(String city, String state) {
        return restaurantRepository.findByCityAndState(city, state).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    private RestaurantResponse mapToResponse(Restaurant restaurant) {
        RestaurantResponse response = new RestaurantResponse();
        response.setId(restaurant.getId());
        response.setUserId(restaurant.getUser().getId());
        response.setRestaurantName(restaurant.getRestaurantName());
        response.setCity(restaurant.getCity());
        response.setState(restaurant.getState());
        response.setZipCode(restaurant.getZipCode());
        response.setLatitude(restaurant.getLatitude());
        response.setLongitude(restaurant.getLongitude());
        response.setOpeningTime(restaurant.getOpeningTime());
        response.setClosingTime(restaurant.getClosingTime());
        response.setImgUrl(restaurant.getImgUrl());
        response.setOpen(restaurant.isOpen());
        response.setMetadata(restaurant.getMetadata());
        response.setCreatedAt(restaurant.getCreatedAt());
        response.setUpdatedAt(restaurant.getUpdatedAt());
        return response;
    }
}
