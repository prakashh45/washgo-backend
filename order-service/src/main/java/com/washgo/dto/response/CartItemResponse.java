package com.washgo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {

    private Long id;

    private Long serviceId;

    private String serviceName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;
}