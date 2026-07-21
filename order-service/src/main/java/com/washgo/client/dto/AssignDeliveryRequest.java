package com.washgo.client.dto;

import lombok.Data;

@Data
public class AssignDeliveryRequest {

    private Long orderId;
    private Long deliveryPartnerId;
    private String legType;
}