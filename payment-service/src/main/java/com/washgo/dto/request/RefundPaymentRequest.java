package com.washgo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefundPaymentRequest {

    @NotBlank
    private String paymentNumber;

    @NotNull
    private Long paymentId;

    private String reason;




}