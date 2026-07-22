package com.washgo.client;

import com.washgo.client.dto.AssignDeliveryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "LOGISTICS-SERVICE")
public interface LogisticsClient {

    @PostMapping("/api/v1/logistics/pickup")
    String assignPickupPartner(@RequestBody AssignDeliveryRequest request);
}