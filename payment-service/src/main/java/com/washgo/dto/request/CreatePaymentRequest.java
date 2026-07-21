package com.washgo.dto.request;

import com.washgo.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePaymentRequest {

    @NotNull
    private Long orderId;

    @NotNull
    private Long customerId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private PaymentMethod paymentMethod;
}