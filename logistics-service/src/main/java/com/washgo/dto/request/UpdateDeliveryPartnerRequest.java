package com.washgo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDeliveryPartnerRequest {

    @NotBlank
    private String vehicleType;

    @NotBlank
    private String vehicleNumber;

    @NotBlank
    private String drivingLicense;

    @NotBlank
    private String aadhaarNumber;
}