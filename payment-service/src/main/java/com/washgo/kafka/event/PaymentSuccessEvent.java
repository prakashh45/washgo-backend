package com.washgo.kafka.event;


import com.washgo.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessEvent {

    private String paymentNumber;

    private Long orderId;

    private Long customerId;

    private BigDecimal amount;

    private String gatewayTransactionId;

    private PaymentStatus paymentStatus;
}