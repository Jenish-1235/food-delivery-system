package org.example.fooddeliverysystem.service;

import org.example.fooddeliverysystem.dto.driver.DriverLocationUpdateRequest;
import org.example.fooddeliverysystem.dto.driver.DriverRequest;
import org.example.fooddeliverysystem.dto.driver.DriverResponse;
import org.example.fooddeliverysystem.exception.ResourceNotFoundException;
import org.example.fooddeliverysystem.model.Driver;
import org.example.fooddeliverysystem.model.User;
import org.example.fooddeliverysystem.repository.DriverRepository;
import org.example.fooddeliverysystem.repository.UserRepository;
import org.example.fooddeliverysystem.util.CacheKeys;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DriverService {
    
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final CacheService cacheService;
    
    public DriverService(DriverRepository driverRepository,
                        UserRepository userRepository,
                        CacheService cacheService) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.cacheService = cacheService;
    }
    
    @Transactional
    public DriverResponse register(String userId, DriverRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        // Check if driver already exists for this user
        driverRepository.findByUser(user).ifPresent(driver -> {
            throw new org.example.fooddeliverysystem.exception.ConflictException("Driver", "user: " + userId);
        });
        
        Driver driver = new Driver(user, request.getLicenseNumber(), request.getVehicleNumber());
        driver = driverRepository.save(driver);
        
        return mapToResponse(driver);
    }
    
    @Transactional
    public void updateLocation(String driverId, DriverLocationUpdateRequest request) {
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new ResourceNotFoundException("Driver", driverId));
        
        // Store location in cache (not in database as per design)
        String locationKey = CacheKeys.driverLocationKey(driverId);
        String locationValue = request.getLatitude() + "," + request.getLongitude();
        cacheService.setStringWithExpiry(
            locationKey,
            locationValue,
            CacheKeys.DRIVER_LOCATION_TTL,
            TimeUnit.SECONDS
        );
    }
    
    @Transactional
    public void setAvailability(String driverId, boolean available) {
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new ResourceNotFoundException("Driver", driverId));
        
        // Store availability in cache (not in database as per design)
        String availabilityKey = CacheKeys.driverAvailabilityKey(driverId);
        cacheService.setStringWithExpiry(
            availabilityKey,
            String.valueOf(available),
            CacheKeys.DRIVER_AVAILABILITY_TTL,
            TimeUnit.SECONDS
        );
    }
    
    public List<DriverResponse> findAvailableDrivers() {
        // This would typically query cache for available drivers
        // For now, return all drivers not on leave
        return driverRepository.findAll().stream()
            .filter(driver -> !driver.isOnLeave())
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public DriverResponse findById(String id) {
        Driver driver = driverRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Driver", id));
        return mapToResponse(driver);
    }
    
    public DriverResponse findByUser(User user) {
        Driver driver = driverRepository.findByUser(user)
            .orElseThrow(() -> new ResourceNotFoundException("Driver", "user: " + user.getId()));
        return mapToResponse(driver);
    }
    
    private DriverResponse mapToResponse(Driver driver) {
        DriverResponse response = new DriverResponse();
        response.setId(driver.getId());
        response.setUserId(driver.getUser().getId());
        response.setLicenseNumber(driver.getLicenseNumber());
        response.setVehicleNumber(driver.getVehicleNumber());
        response.setOnLeave(driver.isOnLeave());
        response.setMetadata(driver.getMetadata());
        response.setCreatedAt(driver.getCreatedAt());
        response.setUpdatedAt(driver.getUpdatedAt());
        return response;
    }
}
