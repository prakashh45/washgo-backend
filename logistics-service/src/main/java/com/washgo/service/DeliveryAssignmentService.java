package com.washgo.service;

import com.washgo.dto.request.AssignDeliveryRequest;
import com.washgo.dto.response.AssignmentResponse;

import java.util.List;

public interface DeliveryAssignmentService {

    // Assignment
    AssignmentResponse assignPartner(AssignDeliveryRequest request);

    // Read
    AssignmentResponse getAssignmentById(Long id);

    List<AssignmentResponse> getAssignmentsByOrder(Long orderId);

    List<AssignmentResponse> getAssignmentsByPartner(Long partnerId);

    // Workflow
    AssignmentResponse acceptAssignment(Long assignmentId);

    AssignmentResponse rejectAssignment(Long assignmentId);

    AssignmentResponse startTrip(Long assignmentId);

    AssignmentResponse arrived(Long assignmentId);

    AssignmentResponse verifyOtp(Long assignmentId, String otp);

    AssignmentResponse completeAssignment(Long assignmentId);

}