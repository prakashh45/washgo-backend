package com.washgo.controller;

import com.washgo.dto.NotificationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @PostMapping("/order-placed")
    public ResponseEntity<String> sendOrderPlacedNotification(
            @RequestBody NotificationRequest request) {

        System.out.println("======================================");
        System.out.println("Order Placed Notification");
        System.out.println("User ID    : " + request.getUserId());
        System.out.println("Recipient  : " + request.getRecipient());
        System.out.println("Title      : " + request.getTitle());
        System.out.println("Message    : " + request.getMessage());
        System.out.println("Type       : " + request.getType());
        System.out.println("======================================");

        return ResponseEntity.ok("Notification Sent Successfully");
    }
}