package com.washgo.controller;

import com.washgo.dto.request.CreateOrderRequest;
import com.washgo.dto.request.UpdateOrderStatusRequest;
import com.washgo.dto.response.OrderResponse;
import com.washgo.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        return orderService.placeOrder(request);
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrderById(@PathVariable Long orderId) {

        return orderService.getOrderById(orderId);
    }

    @GetMapping("/number/{orderNumber}")
    public OrderResponse getOrderByOrderNumber(
            @PathVariable String orderNumber) {

        return orderService.getOrderByOrderNumber(orderNumber);
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderResponse> getCustomerOrders(
            @PathVariable Long customerId) {

        return orderService.getCustomerOrders(customerId);
    }

    @GetMapping("/partner/{partnerId}")
    public List<OrderResponse> getPartnerOrders(
            @PathVariable Long partnerId) {

        return orderService.getPartnerOrders(partnerId);
    }

    @PatchMapping("/{orderId}/status")
    public OrderResponse updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        return orderService.updateOrderStatus(orderId, request);
    }
    @GetMapping("/health")
    public String health() {
        return "Order Service is UP";
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable Long orderId) {

        orderService.cancelOrder(orderId);
    }
}