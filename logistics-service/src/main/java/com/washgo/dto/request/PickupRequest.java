package com.washgo.dto.request;

import lombok.Data;

@Data
public class PickupRequest {

    private Long orderId;
    private Long customerId;
    private Long partnerId;
    private String pickupAddress;
    private String pickupDate;
    private String pickupTime;
}