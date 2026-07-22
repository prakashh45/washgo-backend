package com.washgo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.washgo.dto.request.NotificationRequest;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

    @PostMapping("/api/v1/notifications/order-placed")
    void sendOrderPlacedNotification(
            @RequestBody NotificationRequest request
    );
}