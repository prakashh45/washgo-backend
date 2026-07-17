package com.washgo.service;

import com.washgo.dto.request.CreateOrderRequest;
import com.washgo.dto.request.UpdateOrderStatusRequest;
import com.washgo.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(CreateOrderRequest request);

    OrderResponse getOrderById(Long orderId);

    OrderResponse getOrderByOrderNumber(String orderNumber);

    List<OrderResponse> getCustomerOrders(Long customerId);

    List<OrderResponse> getPartnerOrders(Long partnerId);

    OrderResponse updateOrderStatus(Long orderId,
                                    UpdateOrderStatusRequest request);

    void cancelOrder(Long orderId);
}