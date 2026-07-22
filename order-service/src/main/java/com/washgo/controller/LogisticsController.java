package com.washgo.controller;

import com.washgo.client.dto.AssignDeliveryRequest;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/logistics")
public class LogisticsController {

    @PostMapping("/pickup")
    public String assignPickupPartner(@RequestBody AssignDeliveryRequest request) {

        System.out.println("================================");
        System.out.println("Order ID      : " + request.getOrderId());
        System.out.println("Partner ID    : " + request.getDeliveryPartnerId());
        System.out.println("Leg Type      : " + request.getLegType());
        System.out.println("================================");

        return "Pickup Assigned Successfully";
    }
}