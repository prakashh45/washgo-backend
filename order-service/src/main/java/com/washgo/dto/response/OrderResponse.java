package com.washgo.dto.response;

import com.washgo.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;
import com.washgo.common.enums.OrderStatus;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderResponse {

    private Long id;
    private String orderNumber;
    private Long customerId;
    private Long laundryPartnerId;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> items;
}