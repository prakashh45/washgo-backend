package com.washgo.service.impl;

import com.washgo.dto.request.CreatePaymentRequest;
import com.washgo.dto.request.RefundPaymentRequest;
import com.washgo.dto.request.VerifyPaymentRequest;
import com.washgo.dto.request.VerifySignatureRequest;
import com.washgo.dto.response.PaymentResponse;
import com.washgo.entity.Payment;
import com.washgo.enums.PaymentStatus;
import com.washgo.exception.ResourceNotFoundException;
import com.washgo.repository.PaymentRepository;
import com.washgo.service.PaymentService;
import com.washgo.util.PaymentNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponse createPayment(CreatePaymentRequest request) {

        Payment payment = new Payment();

        payment.setPaymentNumber(PaymentNumberGenerator.generatePaymentNumber());
        payment.setOrderId(request.getOrderId());
        payment.setCustomerId(request.getCustomerId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);

        Payment savedPayment = paymentRepository.save(payment);

        return mapToResponse(savedPayment);
    }

    @Override
    public PaymentResponse verifyPayment(VerifyPaymentRequest request) {

        Payment payment = paymentRepository.findByPaymentNumber(request.getPaymentNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found with number: " + request.getPaymentNumber()));

        payment.setGatewayPaymentId(request.getGatewayPaymentId());
        payment.setGatewayTransactionId(request.getGatewayTransactionId());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        Payment updatedPayment = paymentRepository.save(payment);

        return mapToResponse(updatedPayment);
    }

    @Override
    public PaymentResponse refundPayment(RefundPaymentRequest request) {

        Payment payment = paymentRepository.findByPaymentNumber(request.getPaymentNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found with number: " + request.getPaymentNumber()));

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        payment.setRemarks(request.getReason());

        Payment updatedPayment = paymentRepository.save(payment);

        return mapToResponse(updatedPayment);
    }
    @Override
    public PaymentResponse getPaymentById(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found with id: " + id));

        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByNumber(String paymentNumber) {

        Payment payment = paymentRepository.findByPaymentNumber(paymentNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found with number: " + paymentNumber));

        return mapToResponse(payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsByOrder(Long orderId) {

        return paymentRepository.findByOrderId(orderId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<PaymentResponse> getPaymentsByCustomer(Long customerId) {

        return paymentRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public PaymentResponse completeRazorpayPayment(VerifySignatureRequest request) {

        Payment payment = paymentRepository.findByPaymentNumber(request.getPaymentNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with number: " + request.getPaymentNumber()));

        payment.setGatewayOrderId(request.getRazorpayOrderId());
        payment.setGatewayPaymentId(request.getRazorpayPaymentId());
        payment.setGatewayTransactionId(request.getRazorpayPaymentId());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        Payment saved = paymentRepository.save(payment);

        return mapToResponse(saved);
    }

    private PaymentResponse mapToResponse(Payment payment) {

        PaymentResponse response = new PaymentResponse();

        response.setId(payment.getId());
        response.setPaymentNumber(payment.getPaymentNumber());
        response.setOrderId(payment.getOrderId());
        response.setCustomerId(payment.getCustomerId());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setGatewayTransactionId(payment.getGatewayTransactionId());
        response.setGatewayOrderId(payment.getGatewayOrderId());
        response.setGatewayPaymentId(payment.getGatewayPaymentId());
        response.setRemarks(payment.getRemarks());
        response.setCreatedAt(payment.getCreatedAt());

        return response;
    }
}