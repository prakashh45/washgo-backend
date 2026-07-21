package com.washgo.service.impl;
import com.washgo.client.NotificationClient;
import com.washgo.dto.request.CreateOrderRequest;
import com.washgo.dto.request.OrderItemRequest;
import com.washgo.dto.request.UpdateOrderStatusRequest;
import com.washgo.dto.response.OrderItemResponse;
import com.washgo.dto.response.OrderResponse;
import com.washgo.entity.Order;
import com.washgo.entity.OrderItem;
import com.washgo.enums.OrderStatus;
import com.washgo.enums.PaymentStatus;
import com.washgo.exception.ResourceNotFoundException;
import com.washgo.repository.OrderItemRepository;
import com.washgo.repository.OrderRepository;
import com.washgo.service.OrderService;
import com.washgo.util.OrderNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.washgo.client.LogisticsClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;
    private final NotificationClient notificationClient;

    private final LogisticsClient logisticsClient;

    @Override
    @Transactional
    public OrderResponse placeOrder(CreateOrderRequest request) {

        Order order = new Order();


        order.setOrderNumber(OrderNumberGenerator.generate());
        order.setCustomerId(request.getCustomerId());
        order.setLaundryPartnerId(request.getLaundryPartnerId());
        order.setPickupAddressId(request.getPickupAddressId());

        order.setOrderStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setPaymentMethod(request.getPaymentMethod());

        List<OrderItem> orderItems = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {

            OrderItem item = new OrderItem();

            item.setOrder(order);
            item.setServiceId(itemRequest.getServiceId());
            item.setServiceName(itemRequest.getServiceName());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());

            BigDecimal totalPrice =
                    itemRequest.getUnitPrice()
                            .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            item.setTotalPrice(totalPrice);

            totalAmount = totalAmount.add(totalPrice);

            orderItems.add(item);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

// Logistics
        try {
            logisticsClient.assignPickupPartner(
                    savedOrder.getId(),
                    savedOrder.getLaundryPartnerId()
            );
        } catch (Exception ex) {
            System.out.println("Logistics Service unavailable: " + ex.getMessage());
        }

// Notification
        try {
            notificationClient.sendOrderPlacedNotification(savedOrder);
        } catch (Exception ex) {
            System.out.println("Notification Service unavailable: " + ex.getMessage());
        }

        return mapToResponse(savedOrder);


    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order",
                                "id",
                                orderId));

        return mapToResponse(order);
    }
    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber) {

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order",
                                "orderNumber",
                                orderNumber));

        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getCustomerOrders(Long customerId) {

        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getPartnerOrders(Long partnerId) {

        return orderRepository.findByLaundryPartnerId(partnerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId,
                                           UpdateOrderStatusRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order",
                                "id",
                                orderId));

        order.setOrderStatus(request.getOrderStatus());

        Order updatedOrder = orderRepository.save(order);

        return mapToResponse(updatedOrder);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order",
                                "id",
                                orderId));

        order.setOrderStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);
    }
    private OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> itemResponses = order.getOrderItems()
                .stream()
                .map(item -> OrderItemResponse.builder()
                        .serviceId(item.getServiceId())
                        .serviceName(item.getServiceName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .laundryPartnerId(order.getLaundryPartnerId())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .items(itemResponses)
                .build();
    }
}