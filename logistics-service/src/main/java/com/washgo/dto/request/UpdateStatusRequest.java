package com.washgo.dto.request;

import com.washgo.enums.DeliveryPartnerStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private DeliveryPartnerStatus status;
}
