package com.washgo.controller;

import com.washgo.common.ApiResponse;
import com.washgo.dto.request.CreatePaymentRequest;
import com.washgo.dto.request.RefundPaymentRequest;
import com.washgo.dto.request.VerifyPaymentRequest;
import com.washgo.dto.response.PaymentResponse;
import com.washgo.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ApiResponse<PaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        return new ApiResponse<>(
                true,
                "Payment created successfully",
                paymentService.createPayment(request),
                LocalDateTime.now()
        );
    }

    @PostMapping("/verify")
    public ApiResponse<PaymentResponse> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request) {

        return new ApiResponse<>(
                true,
                "Payment verified successfully",
                paymentService.verifyPayment(request),
                LocalDateTime.now()
        );
    }

    @PostMapping("/refund")
    public ApiResponse<PaymentResponse> refundPayment(
            @Valid @RequestBody RefundPaymentRequest request) {

        return new ApiResponse<>(
                true,
                "Payment refunded successfully",
                paymentService.refundPayment(request),
                LocalDateTime.now()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<PaymentResponse> getPaymentById(@PathVariable Long id) {

        return new ApiResponse<>(
                true,
                "Payment fetched successfully",
                paymentService.getPaymentById(id),
                LocalDateTime.now()
        );
    }

    @GetMapping("/number/{paymentNumber}")
    public ApiResponse<PaymentResponse> getPaymentByNumber(
            @PathVariable String paymentNumber) {

        return new ApiResponse<>(
                true,
                "Payment fetched successfully",
                paymentService.getPaymentByNumber(paymentNumber),
                LocalDateTime.now()
        );
    }

    @GetMapping("/order/{orderId}")
    public ApiResponse<List<PaymentResponse>> getPaymentsByOrder(
            @PathVariable Long orderId) {

        return new ApiResponse<>(
                true,
                "Payments fetched successfully",
                paymentService.getPaymentsByOrder(orderId),
                LocalDateTime.now()
        );
    }

    @GetMapping("/customer/{customerId}")
    public ApiResponse<List<PaymentResponse>> getPaymentsByCustomer(
            @PathVariable Long customerId) {

        return new ApiResponse<>(
                true,
                "Payments fetched successfully",
                paymentService.getPaymentsByCustomer(customerId),
                LocalDateTime.now()
        );
    }
}