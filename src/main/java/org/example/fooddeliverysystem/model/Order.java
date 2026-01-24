package org.example.fooddeliverysystem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.example.fooddeliverysystem.enums.OrderStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Order number is required")
    private String orderNumber;
    
    @Column(nullable = false)
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @Column(nullable = false)
    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;
    
    private String driverId;
    
    @Column(columnDefinition = "jsonb", nullable = false)
    @NotBlank(message = "Items JSON is required")
    private String itemsJson;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Order status is required")
    private OrderStatus orderStatus;
    
    @Column(nullable = false)
    @NotBlank(message = "Address is required")
    private String address;
    
    @Column(nullable = false)
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @Column(nullable = false)
    @NotNull(message = "Longitude is required")
    private Double longitude;
    
    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;
    
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime preparedAt;
    
    private LocalDateTime deliveredAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Order() {}

    public Order(String orderNumber, String userId, String restaurantId, String itemsJson, 
                OrderStatus orderStatus, String address, Double latitude, Double longitude, 
                BigDecimal amount) {
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.itemsJson = itemsJson;
        this.orderStatus = orderStatus;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getItemsJson() {
        return itemsJson;
    }

    public void setItemsJson(String itemsJson) {
        this.itemsJson = itemsJson;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPreparedAt() {
        return preparedAt;
    }

    public void setPreparedAt(LocalDateTime preparedAt) {
        this.preparedAt = preparedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

