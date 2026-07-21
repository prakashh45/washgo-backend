package com.washgo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRazorpayOrderResponse {

    private String razorpayOrderId;

    private String keyId;

    private Integer amount;

    private String currency;
}