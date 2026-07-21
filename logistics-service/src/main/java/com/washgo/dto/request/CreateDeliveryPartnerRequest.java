package com.washgo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDeliveryPartnerRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;

    @NotBlank(message = "Driving license is required")
    private String drivingLicense;

    @NotBlank(message = "Aadhaar number is required")
    private String aadhaarNumber;
}