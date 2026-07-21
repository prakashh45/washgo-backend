package com.washgo.dto.request;

import com.washgo.enums.DeliveryLegType;
import jakarta.validation.constraints.NotNull;

public class AssignDeliveryRequest {

    @NotNull(message = "Order Id is required")
    private Long orderId;

    @NotNull(message = "Delivery Partner Id is required")
    private Long deliveryPartnerId;

    @NotNull(message = "Leg Type is required")
    private DeliveryLegType legType;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getDeliveryPartnerId() {
        return deliveryPartnerId;
    }

    public void setDeliveryPartnerId(Long deliveryPartnerId) {
        this.deliveryPartnerId = deliveryPartnerId;
    }

    public DeliveryLegType getLegType() {
        return legType;
    }

    public void setLegType(DeliveryLegType legType) {
        this.legType = legType;
    }
}