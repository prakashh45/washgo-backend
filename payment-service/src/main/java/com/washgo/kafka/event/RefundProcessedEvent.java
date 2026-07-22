package com.washgo.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundProcessedEvent {

    private String paymentNumber;

    private Long orderId;

    private BigDecimal refundAmount;
}