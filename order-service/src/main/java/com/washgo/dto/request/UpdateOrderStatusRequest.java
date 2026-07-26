package com.washgo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.washgo.common.enums.OrderStatus;

@Data
public class UpdateOrderStatusRequest {

    @NotNull
    private OrderStatus orderStatus;
}