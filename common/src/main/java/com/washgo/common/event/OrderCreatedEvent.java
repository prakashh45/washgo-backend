package com.washgo.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    private Long orderId;
    private String orderNumber;
    private Long customerId;
    private Long laundryPartnerId;

    private String pickupAddress;

    private BigDecimal totalAmount;

    private String paymentMethod;

    private String orderStatus;
}