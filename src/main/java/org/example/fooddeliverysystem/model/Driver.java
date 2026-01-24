package org.example.fooddeliverysystem.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "drivers")
@EntityListeners(AuditingEntityListener.class)
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(unique = true, nullable = false)
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @Column(nullable = false)
    @NotBlank(message = "License number is required")
    private String licenseNumber;
    
    @Column(nullable = false)
    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;
    
    @Column(nullable = false)
    private boolean isAvailable = true;
    
    @Column(nullable = false)
    @NotNull(message = "Current latitude is required")
    private Double currentLatitude;
    
    @Column(nullable = false)
    @NotNull(message = "Current longitude is required")
    private Double currentLongitude;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Driver() {}

    public Driver(String userId, String licenseNumber, String vehicleNumber, 
                 Double currentLatitude, Double currentLongitude) {
        this.userId = userId;
        this.licenseNumber = licenseNumber;
        this.vehicleNumber = vehicleNumber;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
    }

    public Driver(String userId, String licenseNumber, String vehicleNumber, 
                 boolean isAvailable, Double currentLatitude, Double currentLongitude) {
        this.userId = userId;
        this.licenseNumber = licenseNumber;
        this.vehicleNumber = vehicleNumber;
        this.isAvailable = isAvailable;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

