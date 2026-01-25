package org.example.fooddeliverysystem.dto.driver;

import jakarta.validation.constraints.NotBlank;

public class DriverRequest {
    
    @NotBlank(message = "License number is required")
    private String licenseNumber;
    
    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;
    
    public DriverRequest() {}
    
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
}
