package com.washgo.common.event;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSuccessEvent {

    private String paymentNumber;
    private Long orderId;
    private Long customerId;
    private BigDecimal amount;
    private String gatewayTransactionId;
}