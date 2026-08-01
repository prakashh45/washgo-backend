package com.washgo.service.impl;

import com.washgo.dto.request.CreatePaymentRequest;
import com.washgo.dto.request.RefundPaymentRequest;
import com.washgo.dto.request.VerifyPaymentRequest;
import com.washgo.dto.request.VerifySignatureRequest;
import com.washgo.dto.response.PaymentResponse;
import com.washgo.entity.Payment;
import com.washgo.enums.PaymentStatus;
import com.washgo.exception.ResourceNotFoundException;
import com.washgo.kafka.event.PaymentCreatedEvent;
import com.washgo.kafka.event.PaymentSuccessEvent;
import com.washgo.kafka.producer.PaymentEventProducer;
import com.washgo.repository.PaymentRepository;
import com.washgo.service.PaymentService;
import com.washgo.util.PaymentNumberGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;


    @Override
    public PaymentResponse createPayment(CreatePaymentRequest request) {

        if (paymentRepository.existsByOrderId(request.getOrderId())) {
            throw new IllegalStateException(
                    "Payment already exists for order : "
                            + request.getOrderId());
        }

        Payment payment = new Payment();

        payment.setPaymentNumber(
                PaymentNumberGenerator.generatePaymentNumber());

        payment.setOrderId(request.getOrderId());
        payment.setCustomerId(request.getCustomerId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setRemarks(request.getRemarks());

        Payment savedPayment = paymentRepository.save(payment);

        paymentEventProducer.publishPaymentCreated(PaymentCreatedEvent.builder()
                .paymentNumber(savedPayment.getPaymentNumber())
                .orderId(savedPayment.getOrderId())
                .customerId(savedPayment.getCustomerId())
                .amount(savedPayment.getAmount())
                .paymentMethod(savedPayment.getPaymentMethod())
                .paymentStatus(savedPayment.getPaymentStatus())
                .build());

        return mapToResponse(savedPayment);
    }
    @Override
    public PaymentResponse verifyPayment(VerifyPaymentRequest request) {

        Payment payment = paymentRepository
                .findByPaymentNumber(request.getPaymentNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found : "
                                        + request.getPaymentNumber()));

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new IllegalStateException(
                    "Payment already verified.");
        }

        payment.setGatewayPaymentId(
                request.getGatewayPaymentId());

        payment.setGatewayTransactionId(
                request.getGatewayTransactionId());

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());

        Payment updated = paymentRepository.save(payment);
        publishPaymentSuccess(updated);

        return mapToResponse(updated);
    }
    @Override
    public PaymentResponse refundPayment(
            RefundPaymentRequest request) {

        Payment payment = paymentRepository
                .findByPaymentNumber(request.getPaymentNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found"));

        if (payment.getPaymentStatus()
                != PaymentStatus.SUCCESS) {

            throw new IllegalStateException(
                    "Only successful payments can be refunded.");
        }

        payment.setPaymentStatus(PaymentStatus.REFUNDED);

        payment.setRemarks(request.getReason());

        Payment updated = paymentRepository.save(payment);

        return mapToResponse(updated);
    }
    @Override
    public PaymentResponse getPaymentById(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with id : "
                                        + id));

        return mapToResponse(payment);
    }
    @Override
    public PaymentResponse getPaymentByNumber(
            String paymentNumber) {

        Payment payment = paymentRepository
                .findByPaymentNumber(paymentNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found"));

        return mapToResponse(payment);
    }
    @Override
    public List<PaymentResponse> getPaymentsByOrder(
            Long orderId) {

        return paymentRepository.findByOrderId(orderId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public List<PaymentResponse> getPaymentsByCustomer(
            Long customerId) {

        return paymentRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public PaymentResponse completeRazorpayPayment(
            VerifySignatureRequest request) {

        Payment payment = paymentRepository
                .findByPaymentNumber(request.getPaymentNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found"));

        if (payment.getPaymentStatus()
                == PaymentStatus.SUCCESS) {

            throw new IllegalStateException(
                    "Payment already completed.");
        }

        payment.setGatewayOrderId(
                request.getRazorpayOrderId());

        payment.setGatewayPaymentId(
                request.getRazorpayPaymentId());

        payment.setGatewayTransactionId(
                request.getRazorpayPaymentId());

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());

        Payment updated = paymentRepository.save(payment);
        publishPaymentSuccess(updated);

        return mapToResponse(updated);
    }

    private void publishPaymentSuccess(Payment payment) {
        paymentEventProducer.publishPaymentSuccess(PaymentSuccessEvent.builder()
                .paymentNumber(payment.getPaymentNumber())
                .orderId(payment.getOrderId())
                .customerId(payment.getCustomerId())
                .amount(payment.getAmount())
                .gatewayTransactionId(payment.getGatewayTransactionId())
                .build());
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
