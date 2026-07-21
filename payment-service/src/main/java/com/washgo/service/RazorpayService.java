package com.washgo.service;

import com.washgo.dto.request.CreateRazorpayOrderRequest;
import com.washgo.dto.request.VerifySignatureRequest;
import com.washgo.dto.response.CreateRazorpayOrderResponse;

public interface RazorpayService {

    CreateRazorpayOrderResponse createOrder(CreateRazorpayOrderRequest request) throws Exception;

    boolean verifySignature(VerifySignatureRequest request);
}