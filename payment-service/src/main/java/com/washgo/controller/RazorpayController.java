package com.washgo.controller;

import com.washgo.common.ApiResponse;
import com.washgo.dto.request.CreateRazorpayOrderRequest;
import com.washgo.dto.request.VerifySignatureRequest;
import com.washgo.dto.response.CreateRazorpayOrderResponse;
import com.washgo.dto.response.PaymentResponse;
import com.washgo.service.PaymentService;
import com.washgo.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/razorpay")
@RequiredArgsConstructor
public class RazorpayController {

    private final RazorpayService razorpayService;
    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ApiResponse<CreateRazorpayOrderResponse> createOrder(
            @RequestBody CreateRazorpayOrderRequest request) throws Exception {

        return new ApiResponse<>(
                true,
                "Razorpay order created successfully",
                razorpayService.createOrder(request),
                LocalDateTime.now()
        );
    }

    @PostMapping("/verify")
    public ApiResponse<PaymentResponse> verifyPayment(
            @RequestBody VerifySignatureRequest request) {

        boolean verified = razorpayService.verifySignature(request);

        if (!verified) {
            throw new RuntimeException("Invalid Razorpay Signature");
        }

        return new ApiResponse<>(
                true,
                "Payment verified successfully",
                paymentService.completeRazorpayPayment(request),
                LocalDateTime.now()
        );
    }
}