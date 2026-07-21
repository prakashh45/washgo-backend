package com.washgo.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.washgo.dto.request.CreateRazorpayOrderRequest;
import com.washgo.dto.request.VerifySignatureRequest;
import com.washgo.dto.response.CreateRazorpayOrderResponse;
import com.washgo.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl implements RazorpayService {

    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    @Value("${razorpay.currency}")
    private String currency;

    @Override
    public CreateRazorpayOrderResponse createOrder(CreateRazorpayOrderRequest request) throws Exception {

        JSONObject orderRequest = new JSONObject();

        orderRequest.put("amount", request.getAmount());
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", "ORDER_" + request.getOrderId());

        Order order = razorpayClient.orders.create(orderRequest);

        return CreateRazorpayOrderResponse.builder()
                .razorpayOrderId(order.get("id").toString())
                .keyId(keyId)
                .amount(request.getAmount())
                .currency(currency)
                .build();
    }

    @Override
    public boolean verifySignature(VerifySignatureRequest request) {

        try {

            JSONObject attributes = new JSONObject();

            attributes.put("razorpay_order_id", request.getRazorpayOrderId());
            attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
            attributes.put("razorpay_signature", request.getRazorpaySignature());

            return Utils.verifyPaymentSignature(attributes, keySecret);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}