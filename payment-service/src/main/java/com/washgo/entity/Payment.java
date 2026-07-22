package com.washgo.entity;

import com.washgo.enums.PaymentMethod;
import com.washgo.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String paymentNumber;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    private String gatewayTransactionId;

    private String gatewayOrderId;

    private String gatewayPaymentId;

    @Column(nullable = false, length = 3)
    private String currency = "INR";

    private String gatewayName;

    private String failureReason;

    private String receiptUrl;

    private LocalDateTime paidAt;

    @Column(length = 500)
    private String remarks;
}