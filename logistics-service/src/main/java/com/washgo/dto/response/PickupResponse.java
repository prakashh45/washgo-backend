package com.washgo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PickupResponse {

    private Long orderId;
    private String pickupId;
    private String status;
    private String message;
}