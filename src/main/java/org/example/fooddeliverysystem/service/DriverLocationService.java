package org.example.fooddeliverysystem.service;

import org.example.fooddeliverysystem.dto.driver.DriverLocationUpdateRequest;
import org.example.fooddeliverysystem.exception.ResourceNotFoundException;
import org.example.fooddeliverysystem.model.Driver;
import org.example.fooddeliverysystem.repository.DriverRepository;
import org.example.fooddeliverysystem.util.CacheKeys;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class DriverLocationService {
    
    private final DriverRepository driverRepository;
    private final CacheService cacheService;
    
    public DriverLocationService(DriverRepository driverRepository, CacheService cacheService) {
        this.driverRepository = driverRepository;
        this.cacheService = cacheService;
    }
    
    @Transactional
    public void updateDriverLocation(String driverId, DriverLocationUpdateRequest request) {
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new ResourceNotFoundException("Driver", driverId));
        
        // Store location in Redis cache
        String locationKey = CacheKeys.driverLocationKey(driverId);
        String locationValue = request.getLatitude() + "," + request.getLongitude();
        cacheService.setStringWithExpiry(
            locationKey,
            locationValue,
            CacheKeys.DRIVER_LOCATION_TTL,
            TimeUnit.SECONDS
        );
    }
    
    public String getDriverLocation(String driverId) {
        String locationKey = CacheKeys.driverLocationKey(driverId);
        return cacheService.getString(locationKey);
    }
    
    public boolean isDriverAvailable(String driverId) {
        String availabilityKey = CacheKeys.driverAvailabilityKey(driverId);
        String available = cacheService.getString(availabilityKey);
        return available != null && Boolean.parseBoolean(available);
    }
    
    @Transactional
    public void setDriverAvailability(String driverId, boolean available) {
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new ResourceNotFoundException("Driver", driverId));
        
        // Store availability in Redis cache
        String availabilityKey = CacheKeys.driverAvailabilityKey(driverId);
        cacheService.setStringWithExpiry(
            availabilityKey,
            String.valueOf(available),
            CacheKeys.DRIVER_AVAILABILITY_TTL,
            TimeUnit.SECONDS
        );
    }
}
