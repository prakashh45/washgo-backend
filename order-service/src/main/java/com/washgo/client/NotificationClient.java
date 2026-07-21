package com.washgo.client;

import com.washgo.dto.request.NotificationRequest;
import com.washgo.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class NotificationClient {

    private final WebClient webClient;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    public void sendOrderPlacedNotification(Order order) {

        NotificationRequest request = NotificationRequest.builder()
                .userId(String.valueOf(order.getCustomerId()))
                .recipient("customer@example.com") // Temporary
                .title("Order Placed")
                .message("Your order " + order.getOrderNumber() + " has been placed successfully.")
                .type("EMAIL")
                .build();

        webClient.post()
                .uri(notificationServiceUrl + "/api/v1/notifications/send")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}