package org.example.fooddeliverysystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fooddeliverysystem.dto.order.OrderItemRequest;
import org.example.fooddeliverysystem.dto.order.OrderRequest;
import org.example.fooddeliverysystem.dto.order.OrderResponse;
import org.example.fooddeliverysystem.enums.OrderStatus;
import org.example.fooddeliverysystem.exception.BusinessException;
import org.example.fooddeliverysystem.exception.ResourceNotFoundException;
import org.example.fooddeliverysystem.exception.ValidationException;
import org.example.fooddeliverysystem.model.FoodItem;
import org.example.fooddeliverysystem.model.Order;
import org.example.fooddeliverysystem.model.Restaurant;
import org.example.fooddeliverysystem.model.User;
import org.example.fooddeliverysystem.repository.FoodItemRepository;
import org.example.fooddeliverysystem.repository.OrderRepository;
import org.example.fooddeliverysystem.repository.RestaurantRepository;
import org.example.fooddeliverysystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodItemRepository foodItemRepository;
    private final ObjectMapper objectMapper;
    
    public OrderService(OrderRepository orderRepository,
                       UserRepository userRepository,
                       RestaurantRepository restaurantRepository,
                       FoodItemRepository foodItemRepository,
                       ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.foodItemRepository = foodItemRepository;
        this.objectMapper = objectMapper;
    }
    
    @Transactional
    public OrderResponse createOrder(String userId, OrderRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant", request.getRestaurantId()));
        
        if (!restaurant.isOpen()) {
            throw new BusinessException("Restaurant is currently closed");
        }
        
        // Validate and calculate order total
        Double totalAmount = calculateOrderTotal(request.getItems(), restaurant.getId());
        
        // Convert items to JSON
        String itemsJson;
        try {
            itemsJson = objectMapper.writeValueAsString(request.getItems());
        } catch (JsonProcessingException e) {
            throw new ValidationException("Failed to serialize order items");
        }
        
        // Generate order number
        String orderNumber = generateOrderNumber();
        
        Order order = new Order(
            orderNumber,
            user,
            restaurant,
            itemsJson,
            OrderStatus.PENDING,
            request.getAddress(),
            request.getLatitude(),
            request.getLongitude(),
            totalAmount
        );
        
        order = orderRepository.save(order);
        return mapToResponse(order);
    }
    
    public OrderResponse findById(String id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return mapToResponse(order);
    }
    
    public List<OrderResponse> findByUser(User user) {
        return orderRepository.findByUser(user).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public OrderResponse updateStatus(String id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        
        validateStatusTransition(order.getOrderStatus(), newStatus);
        
        order.setOrderStatus(newStatus);
        
        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }
        
        order = orderRepository.save(order);
        return mapToResponse(order);
    }
    
    private Double calculateOrderTotal(List<OrderItemRequest> items, String restaurantId) {
        Double total = 0.0;
        
        for (OrderItemRequest item : items) {
            FoodItem foodItem = foodItemRepository.findById(item.getFoodItemId())
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem", item.getFoodItemId()));
            
            if (foodItem.isDeleted()) {
                throw new BusinessException("Food item " + foodItem.getName() + " is no longer available");
            }
            
            if (!foodItem.isAvailable()) {
                throw new BusinessException("Food item " + foodItem.getName() + " is currently unavailable");
            }
            
            if (!foodItem.getRestaurant().getId().equals(restaurantId)) {
                throw new ValidationException("Food item " + foodItem.getName() + " does not belong to the selected restaurant");
            }
            
            total += foodItem.getPrice() * item.getQuantity();
        }
        
        return total;
    }
    
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case PENDING:
                if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.CANCELLED) {
                    throw new BusinessException("Invalid status transition from PENDING to " + newStatus);
                }
                break;
            case CONFIRMED:
                if (newStatus != OrderStatus.PREPARING && newStatus != OrderStatus.CANCELLED) {
                    throw new BusinessException("Invalid status transition from CONFIRMED to " + newStatus);
                }
                break;
            case PREPARING:
                if (newStatus != OrderStatus.READY && newStatus != OrderStatus.CANCELLED) {
                    throw new BusinessException("Invalid status transition from PREPARING to " + newStatus);
                }
                break;
            case READY:
                if (newStatus != OrderStatus.OUT_FOR_DELIVERY && newStatus != OrderStatus.CANCELLED) {
                    throw new BusinessException("Invalid status transition from READY to " + newStatus);
                }
                break;
            case OUT_FOR_DELIVERY:
                if (newStatus != OrderStatus.DELIVERED && newStatus != OrderStatus.CANCELLED) {
                    throw new BusinessException("Invalid status transition from OUT_FOR_DELIVERY to " + newStatus);
                }
                break;
            case DELIVERED:
            case CANCELLED:
                throw new BusinessException("Cannot change status of " + currentStatus + " order");
        }
    }
    
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + timestamp + "-" + random;
    }
    
    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setUserId(order.getUser().getId());
        response.setRestaurantId(order.getRestaurant().getId());
        response.setDriverId(order.getDriver() != null ? order.getDriver().getId() : null);
        response.setItemsJson(order.getItemsJson());
        response.setOrderStatus(order.getOrderStatus());
        response.setAddress(order.getAddress());
        response.setLatitude(order.getLatitude());
        response.setLongitude(order.getLongitude());
        response.setAmount(order.getAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setDeliveredAt(order.getDeliveredAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setMetadata(order.getMetadata());
        return response;
    }
}
