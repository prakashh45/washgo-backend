package com.washgo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefundPaymentRequest {

    @NotBlank
    private String paymentNumber;

    private String reason;
}