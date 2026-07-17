package com.washgo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCartRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private Long laundryPartnerId;
}