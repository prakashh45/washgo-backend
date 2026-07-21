package com.washgo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRazorpayOrderRequest {

    @NotNull
    private Long orderId;

    @NotNull
    private Long customerId;

    @NotNull
    private Integer amount; // Amount in paise
}