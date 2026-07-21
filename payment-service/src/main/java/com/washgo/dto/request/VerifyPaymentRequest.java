package com.washgo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyPaymentRequest {

    @NotBlank
    private String paymentNumber;

    @NotBlank
    private String gatewayPaymentId;

    @NotBlank
    private String gatewayTransactionId;
}