package com.washgo.dto.response;

import com.washgo.enums.PaymentMethod;
import com.washgo.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {

    private Long id;

    private String paymentNumber;

    private Long orderId;

    private Long customerId;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private String gatewayTransactionId;

    private String gatewayOrderId;

    private String gatewayPaymentId;

    private String remarks;

    private LocalDateTime createdAt;
}