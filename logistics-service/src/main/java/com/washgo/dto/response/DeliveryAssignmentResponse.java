package com.washgo.dto.response;

import com.washgo.enums.AssignmentStatus;
import com.washgo.enums.DeliveryLegType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DeliveryAssignmentResponse {

    private Long id;

    private Long orderId;

    private Long deliveryPartnerId;

    private String deliveryPartnerName;

    private DeliveryLegType legType;

    private AssignmentStatus status;

    private LocalDateTime assignedAt;

    private LocalDateTime completedAt;

    private String remarks;
}