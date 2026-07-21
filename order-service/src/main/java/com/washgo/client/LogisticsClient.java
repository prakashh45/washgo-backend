package com.washgo.client;

import com.washgo.client.dto.AssignDeliveryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class LogisticsClient {

    private final RestClient restClient;

    @Value("${logistics.base-url}")
    private String logisticsBaseUrl;

    public void assignPickupPartner(Long orderId,
                                    Long deliveryPartnerId) {

        AssignDeliveryRequest request = new AssignDeliveryRequest();

        request.setOrderId(orderId);
        request.setDeliveryPartnerId(deliveryPartnerId);
        request.setLegType("PICKUP");

        restClient.post()
                .uri(logisticsBaseUrl + "/api/v1/assignments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}