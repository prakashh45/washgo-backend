package com.washgo.dto.response;

import com.washgo.enums.DeliveryPartnerStatus;
import lombok.Data;

@Data
public class DeliveryPartnerResponse {

    private Long id;
    private Long userId;
    private String vehicleType;
    private String vehicleNumber;
    private String drivingLicense;
    private String aadhaarNumber;
    private Double currentLatitude;
    private Double currentLongitude;
    private Double rating;
    private Integer totalDeliveries;
    private Boolean available;
    private Boolean verified;
    private DeliveryPartnerStatus status;
}