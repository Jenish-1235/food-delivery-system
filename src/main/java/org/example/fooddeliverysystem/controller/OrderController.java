package org.example.fooddeliverysystem.controller;

import jakarta.validation.Valid;
import org.example.fooddeliverysystem.dto.order.OrderRequest;
import org.example.fooddeliverysystem.dto.order.OrderResponse;
import org.example.fooddeliverysystem.dto.order.OrderStatusUpdateRequest;
import org.example.fooddeliverysystem.enums.OrderStatus;
import org.example.fooddeliverysystem.model.User;
import org.example.fooddeliverysystem.service.OrderService;
import org.example.fooddeliverysystem.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderService orderService;
    private final UserService userService;
    
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        OrderResponse response = orderService.createOrder(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'RESTAURANT', 'DELIVERY_PARTNER', 'ADMIN')")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String id) {
        OrderResponse response = orderService.findById(id);
        
        // Verify access
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            String role = currentUser.getRole().name();
            if (!role.equals("ADMIN")) {
                if (role.equals("USER") && !response.getUserId().equals(currentUser.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                // RESTAURANT and DELIVERY_PARTNER can access their orders
            }
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'RESTAURANT', 'DELIVERY_PARTNER', 'ADMIN')")
    public ResponseEntity<List<OrderResponse>> getOrders() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<OrderResponse> orders = orderService.findByUser(currentUser);
        return ResponseEntity.ok(orders);
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('RESTAURANT', 'DELIVERY_PARTNER', 'ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable String id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderResponse response = orderService.updateStatus(id, request.getOrderStatus());
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/assign-driver")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<OrderResponse> assignDriver(
            @PathVariable String id,
            @RequestParam String driverId) {
        OrderResponse response = orderService.assignDriver(id, driverId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('USER', 'RESTAURANT', 'DELIVERY_PARTNER', 'ADMIN')")
    public ResponseEntity<List<OrderResponse>> getOrderHistory() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<OrderResponse> orders = orderService.findByUser(currentUser);
        return ResponseEntity.ok(orders);
    }
}
