package com.washgo.integration;

import com.washgo.client.LogisticsClient;
import com.washgo.client.dto.AssignDeliveryRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor

public class LogisticsIntegrationService {

    private final LogisticsClient logisticsClient;
    private static final Logger log =
            LoggerFactory.getLogger(LogisticsIntegrationService.class);

    @CircuitBreaker(
            name = "logisticsService",
            fallbackMethod = "assignPickupFallback"
    )
    public void assignPickup(AssignDeliveryRequest request) {

        logisticsClient.assignPickupPartner(request);

    }

    public void assignPickupFallback(
            AssignDeliveryRequest request,
            Exception ex) {

        log.warn("==================================");
        log.warn("Logistics Service is DOWN");
        log.warn("Executing fallback...");
        log.warn("Order Id : {}", request.getOrderId());
        log.warn("Reason : {}", ex.getMessage());
        log.warn("==================================");
    }

}