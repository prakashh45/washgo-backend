package com.washgo.dto.request;

import com.washgo.enums.DeliveryPartnerStatus;
import com.washgo.enums.DeliveryPartnerStatus;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    private DeliveryPartnerStatus status;
}