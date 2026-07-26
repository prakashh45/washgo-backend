package com.washgo.common.event;

import com.washgo.common.enums.OrderStatus;
import com.washgo.common.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    private UUID eventId;

    private Long orderId;

    private Long customerId;

    private Long laundryPartnerId;

    private String orderNumber;

    private String pickupAddress;

    private String deliveryAddress;

    private BigDecimal totalAmount;

    private PaymentMethod paymentMethod;

    private OrderStatus orderStatus;

    private LocalDateTime createdAt;
}