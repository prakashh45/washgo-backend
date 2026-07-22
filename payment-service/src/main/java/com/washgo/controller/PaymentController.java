package com.washgo.controller;

import com.washgo.dto.request.CreatePaymentRequest;
import com.washgo.dto.request.RefundPaymentRequest;
import com.washgo.dto.request.VerifyPaymentRequest;
import com.washgo.dto.request.VerifySignatureRequest;
import com.washgo.dto.response.PaymentResponse;
import com.washgo.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createPayment(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentResponse> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request) {

        return ResponseEntity.ok(paymentService.verifyPayment(request));
    }

    @PostMapping("/refund")
    public ResponseEntity<PaymentResponse> refundPayment(
            @Valid @RequestBody RefundPaymentRequest request) {

        return ResponseEntity.ok(paymentService.refundPayment(request));
    }

    @PostMapping("/razorpay/complete")
    public ResponseEntity<PaymentResponse> completeRazorpayPayment(
            @Valid @RequestBody VerifySignatureRequest request) {

        return ResponseEntity.ok(
                paymentService.completeRazorpayPayment(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/number/{paymentNumber}")
    public ResponseEntity<PaymentResponse> getPaymentByNumber(
            @PathVariable String paymentNumber) {

        return ResponseEntity.ok(
                paymentService.getPaymentByNumber(paymentNumber));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByOrder(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByOrder(orderId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByCustomer(customerId));
    }
}