package org.example.fooddeliverysystem.service;

import org.example.fooddeliverysystem.dto.restaurant.RestaurantRequest;
import org.example.fooddeliverysystem.dto.restaurant.RestaurantResponse;
import org.example.fooddeliverysystem.dto.event.AnalyticsEvent;
import org.example.fooddeliverysystem.exception.ResourceNotFoundException;
import org.example.fooddeliverysystem.model.Restaurant;
import org.example.fooddeliverysystem.model.User;
import org.example.fooddeliverysystem.repository.RestaurantRepository;
import org.example.fooddeliverysystem.repository.UserRepository;
import org.example.fooddeliverysystem.util.CacheKeys;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RestaurantService {
    
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final CacheService cacheService;
    private final MetricsService metricsService;
    private final KafkaEventProducer kafkaEventProducer;
    
    public RestaurantService(RestaurantRepository restaurantRepository, 
                            UserRepository userRepository,
                            CacheService cacheService,
                            MetricsService metricsService,
                            KafkaEventProducer kafkaEventProducer) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.cacheService = cacheService;
        this.metricsService = metricsService;
        this.kafkaEventProducer = kafkaEventProducer;
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
        RestaurantResponse response = mapToResponse(restaurant);
        
        // Update cache
        cacheService.setWithExpiry(
            CacheKeys.restaurantKey(restaurant.getId()),
            response,
            CacheKeys.RESTAURANT_TTL,
            TimeUnit.SECONDS
        );
        
        // Publish analytics event
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("action", "restaurant_updated");
        metrics.put("restaurantId", restaurant.getId());
        AnalyticsEvent event = new AnalyticsEvent("RESTAURANT_UPDATED", "RESTAURANT", restaurant.getId(), metrics);
        kafkaEventProducer.publishAnalyticsEvent(event);
        
        return response;
    }
    
    public RestaurantResponse findById(String id) {
        // Try cache first
        RestaurantResponse cached = cacheService.get(CacheKeys.restaurantKey(id), RestaurantResponse.class);
        if (cached != null) {
            return cached;
        }
        
        // Cache miss - fetch from database
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant", id));
        RestaurantResponse response = mapToResponse(restaurant);
        
        // Cache the response
        cacheService.setWithExpiry(
            CacheKeys.restaurantKey(id),
            response,
            CacheKeys.RESTAURANT_TTL,
            TimeUnit.SECONDS
        );
        
        return response;
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
    
    public List<RestaurantResponse> findByCityAndIsOpen(String city) {
        return restaurantRepository.findByCityAndIsOpenTrue(city).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<RestaurantResponse> findByCityAndStateAndIsOpen(String city, String state) {
        return restaurantRepository.findByCityAndStateAndIsOpenTrue(city, state).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public Page<RestaurantResponse> findByCityWithPagination(String city, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return restaurantRepository.findByCity(city, pageable)
            .map(this::mapToResponse);
    }
    
    public Page<RestaurantResponse> findByCityAndStateWithPagination(String city, String state, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return restaurantRepository.findByCityAndState(city, state, pageable)
            .map(this::mapToResponse);
    }
    
    public Page<RestaurantResponse> findOpenRestaurantsWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return restaurantRepository.findByIsOpenTrue(pageable)
            .map(this::mapToResponse);
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
