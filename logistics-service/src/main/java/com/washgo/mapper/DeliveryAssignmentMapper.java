package com.washgo.mapper;

import com.washgo.dto.response.AssignmentResponse;
import com.washgo.entity.DeliveryAssignment;
import org.springframework.stereotype.Component;

@Component
public class DeliveryAssignmentMapper {

    public AssignmentResponse toResponse(DeliveryAssignment assignment) {

        AssignmentResponse response = new AssignmentResponse();

        response.setId(assignment.getId());
        response.setOrderId(assignment.getOrderId());

        response.setDeliveryPartnerId(
                assignment.getDeliveryPartner().getId()
        );

        response.setLegType(assignment.getLegType());
        response.setStatus(assignment.getStatus());

        response.setAssignedAt(assignment.getAssignedAt());
        response.setAcceptedAt(assignment.getAcceptedAt());
        response.setCompletedAt(assignment.getCompletedAt());

        return response;
    }
}