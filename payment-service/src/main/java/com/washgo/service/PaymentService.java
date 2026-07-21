package com.washgo.service;

import com.washgo.dto.request.CreatePaymentRequest;
import com.washgo.dto.request.RefundPaymentRequest;
import com.washgo.dto.request.VerifyPaymentRequest;
import com.washgo.dto.request.VerifySignatureRequest;
import com.washgo.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(CreatePaymentRequest request);

    PaymentResponse verifyPayment(VerifyPaymentRequest request);

    PaymentResponse refundPayment(RefundPaymentRequest request);

    PaymentResponse getPaymentById(Long id);

    PaymentResponse getPaymentByNumber(String paymentNumber);

    List<PaymentResponse> getPaymentsByOrder(Long orderId);

    List<PaymentResponse> getPaymentsByCustomer(Long customerId);

    PaymentResponse completeRazorpayPayment(VerifySignatureRequest request);

}