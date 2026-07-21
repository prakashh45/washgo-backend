package com.washgo.service.impl;

import com.washgo.dto.request.AssignDeliveryRequest;
import com.washgo.dto.response.AssignmentResponse;
import com.washgo.entity.DeliveryAssignment;
import com.washgo.entity.DeliveryPartner;
import com.washgo.enums.AssignmentStatus;
import com.washgo.enums.DeliveryLegType;
import com.washgo.enums.DeliveryPartnerStatus;
import com.washgo.mapper.DeliveryAssignmentMapper;
import com.washgo.repository.DeliveryAssignmentRepository;
import com.washgo.repository.DeliveryPartnerRepository;
import com.washgo.service.DeliveryAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.washgo.enums.DeliveryLegType;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryAssignmentServiceImpl implements DeliveryAssignmentService {

    private final DeliveryAssignmentRepository assignmentRepository;

    private final DeliveryPartnerRepository partnerRepository;

    private final DeliveryAssignmentMapper mapper;

    @Override
    public AssignmentResponse assignPartner(AssignDeliveryRequest request) {

        if (assignmentRepository.existsByOrderIdAndLegType(
                request.getOrderId(),
                request.getLegType())) {

            throw new RuntimeException(
                    "Assignment already exists for this order.");
        }

        DeliveryPartner partner = partnerRepository
                .findById(request.getDeliveryPartnerId())
                .orElseThrow(() ->
                        new RuntimeException("Delivery Partner not found"));

        if (partner.getStatus() != DeliveryPartnerStatus.ACTIVE) {
            throw new RuntimeException(
                    "Delivery Partner is not available.");
        }

        DeliveryAssignment assignment = new DeliveryAssignment();

        assignment.setOrderId(request.getOrderId());
        assignment.setDeliveryPartner(partner);
        assignment.setLegType(request.getLegType());
        assignment.setStatus(AssignmentStatus.ASSIGNED);
        assignment.setAssignedAt(LocalDateTime.now());

        assignment.setPickupOtp(generateOtp());
        assignment.setDeliveryOtp(generateOtp());

        assignmentRepository.save(assignment);

        partner.setStatus(DeliveryPartnerStatus.BUSY);
        partnerRepository.save(partner);

        return mapper.toResponse(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public AssignmentResponse getAssignmentById(Long id) {

        DeliveryAssignment assignment = assignmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Assignment not found"));

        return mapper.toResponse(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignmentsByOrder(Long orderId) {

        return assignmentRepository.findByOrderId(orderId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignmentsByPartner(Long partnerId) {

        return assignmentRepository.findByDeliveryPartnerId(partnerId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
    @Override
    public AssignmentResponse acceptAssignment(Long assignmentId) {

        DeliveryAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() ->
                        new RuntimeException("Assignment not found"));

        if (assignment.getStatus() != AssignmentStatus.ASSIGNED) {
            throw new RuntimeException("Assignment already processed.");
        }

        assignment.setStatus(AssignmentStatus.ACCEPTED);
        assignment.setAcceptedAt(LocalDateTime.now());

        assignmentRepository.save(assignment);

        return mapper.toResponse(assignment);
    }

    @Override
    public AssignmentResponse rejectAssignment(Long assignmentId) {

        DeliveryAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() ->
                        new RuntimeException("Assignment not found"));

        if (assignment.getStatus() != AssignmentStatus.ASSIGNED) {
            throw new RuntimeException("Only assigned deliveries can be rejected.");
        }

        assignment.setStatus(AssignmentStatus.REJECTED);

        DeliveryPartner partner = assignment.getDeliveryPartner();
        partner.setStatus(DeliveryPartnerStatus.ACTIVE);

        partnerRepository.save(partner);
        assignmentRepository.save(assignment);

        return mapper.toResponse(assignment);
    }

    @Override
    public AssignmentResponse startTrip(Long assignmentId) {

        DeliveryAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() ->
                        new RuntimeException("Assignment not found"));

        if (assignment.getStatus() != AssignmentStatus.ACCEPTED) {
            throw new RuntimeException("Trip can only start after assignment is accepted.");
        }

        assignment.setStatus(AssignmentStatus.EN_ROUTE);
        assignment.setStartedAt(LocalDateTime.now());

        assignmentRepository.save(assignment);

        return mapper.toResponse(assignment);
    }

    @Override
    public AssignmentResponse arrived(Long assignmentId) {

        DeliveryAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() ->
                        new RuntimeException("Assignment not found"));

        if (assignment.getStatus() != AssignmentStatus.EN_ROUTE) {
            throw new RuntimeException("Partner has not started the trip yet.");
        }

        assignment.setStatus(AssignmentStatus.ARRIVED);
        assignment.setArrivedAt(LocalDateTime.now());

        assignmentRepository.save(assignment);

        return mapper.toResponse(assignment);
    }
    @Override
    public AssignmentResponse verifyOtp(Long assignmentId, String otp) {

        DeliveryAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() ->
                        new RuntimeException("Assignment not found"));

        if (assignment.getStatus() != AssignmentStatus.ARRIVED) {
            throw new RuntimeException("Partner has not arrived yet.");
        }

        String expectedOtp;

        if (assignment.getLegType() == DeliveryLegType.PICKUP) {
            expectedOtp = assignment.getPickupOtp();
        } else {
            expectedOtp = assignment.getDeliveryOtp();
        }

        if (!expectedOtp.equals(otp)) {
            throw new RuntimeException("Invalid OTP.");
        }

        assignment.setStatus(AssignmentStatus.OTP_VERIFIED);

        assignmentRepository.save(assignment);

        return mapper.toResponse(assignment);
    }

    @Override
    public AssignmentResponse completeAssignment(Long assignmentId) {

        DeliveryAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() ->
                        new RuntimeException("Assignment not found"));

        if (assignment.getStatus() != AssignmentStatus.OTP_VERIFIED) {
            throw new RuntimeException(
                    "OTP must be verified before completing delivery.");
        }

        assignment.setStatus(AssignmentStatus.COMPLETED);
        assignment.setCompletedAt(LocalDateTime.now());

        DeliveryPartner partner = assignment.getDeliveryPartner();
        partner.setStatus(DeliveryPartnerStatus.ACTIVE);

        partnerRepository.save(partner);
        assignmentRepository.save(assignment);

        // TODO: Publish DeliveryConfirmedEvent to Order/Payment Service

        return mapper.toResponse(assignment);
    }

    private String generateOtp() {

        int otp = (int) (Math.random() * 9000) + 1000;

        return String.valueOf(otp);
    }

}