package org.example.fooddeliverysystem.controller;

import jakarta.validation.Valid;
import org.example.fooddeliverysystem.dto.driver.DriverLocationUpdateRequest;
import org.example.fooddeliverysystem.dto.driver.DriverRequest;
import org.example.fooddeliverysystem.dto.driver.DriverResponse;
import org.example.fooddeliverysystem.model.User;
import org.example.fooddeliverysystem.service.DriverService;
import org.example.fooddeliverysystem.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {
    
    private final DriverService driverService;
    private final UserService userService;
    
    public DriverController(DriverService driverService, UserService userService) {
        this.driverService = driverService;
        this.userService = userService;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('DELIVERY_PARTNER') or hasRole('ADMIN')")
    public ResponseEntity<DriverResponse> registerDriver(@Valid @RequestBody DriverRequest request) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        DriverResponse response = driverService.register(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DELIVERY_PARTNER', 'ADMIN')")
    public ResponseEntity<DriverResponse> getDriver(@PathVariable String id) {
        DriverResponse response = driverService.findById(id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/location")
    @PreAuthorize("hasRole('DELIVERY_PARTNER') or hasRole('ADMIN')")
    public ResponseEntity<Void> updateLocation(
            @PathVariable String id,
            @Valid @RequestBody DriverLocationUpdateRequest request) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Verify driver ownership
        DriverResponse driver = driverService.findById(id);
        if (!driver.getUserId().equals(currentUser.getId()) && 
            !currentUser.getRole().name().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        driverService.updateLocation(id, request);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/availability")
    @PreAuthorize("hasRole('DELIVERY_PARTNER') or hasRole('ADMIN')")
    public ResponseEntity<Void> setAvailability(
            @PathVariable String id,
            @RequestParam boolean available) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Verify driver ownership
        DriverResponse driver = driverService.findById(id);
        if (!driver.getUserId().equals(currentUser.getId()) && 
            !currentUser.getRole().name().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        driverService.setAvailability(id, available);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('RESTAURANT', 'ADMIN')")
    public ResponseEntity<List<DriverResponse>> getAvailableDrivers() {
        List<DriverResponse> drivers = driverService.findAvailableDrivers();
        return ResponseEntity.ok(drivers);
    }
}
