package com.washgo.dto.request;

import lombok.Data;

@Data
public class VerifySignatureRequest {

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;

    private String paymentNumber;
}