package com.washgo.dto.request;

import com.washgo.common.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private Long laundryPartnerId;

    @NotNull
    private Long pickupAddressId;

    @NotNull
    private PaymentMethod paymentMethod;

    @NotNull
    private List<OrderItemRequest> items;
}