package com.washgo.controller;

import com.washgo.common.ApiResponse;
import com.washgo.dto.request.AssignDeliveryRequest;
import com.washgo.dto.request.VerifyOtpRequest;
import com.washgo.dto.response.AssignmentResponse;
import com.washgo.service.DeliveryAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class DeliveryAssignmentController {

    private final DeliveryAssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<AssignmentResponse>> assignPartner(
            @Valid @RequestBody AssignDeliveryRequest request) {

        AssignmentResponse response = assignmentService.assignPartner(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AssignmentResponse>builder()
                        .success(true)
                        .message("Delivery partner assigned successfully.")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssignmentResponse>> getAssignment(
            @PathVariable Long id) {

        AssignmentResponse response =
                assignmentService.getAssignmentById(id);

        return ResponseEntity.ok(
                ApiResponse.<AssignmentResponse>builder()
                        .success(true)
                        .message("Assignment fetched successfully.")
                        .data(response)
                        .build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getAssignmentsByOrder(
            @PathVariable Long orderId) {

        List<AssignmentResponse> response =
                assignmentService.getAssignmentsByOrder(orderId);

        return ResponseEntity.ok(
                ApiResponse.<List<AssignmentResponse>>builder()
                        .success(true)
                        .message("Assignments fetched successfully.")
                        .data(response)
                        .build());
    }

    @GetMapping("/partner/{partnerId}")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getAssignmentsByPartner(
            @PathVariable Long partnerId) {

        List<AssignmentResponse> response =
                assignmentService.getAssignmentsByPartner(partnerId);

        return ResponseEntity.ok(
                ApiResponse.<List<AssignmentResponse>>builder()
                        .success(true)
                        .message("Partner assignments fetched successfully.")
                        .data(response)
                        .build());
    }
    @PutMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<AssignmentResponse>> acceptAssignment(
            @PathVariable Long id) {

        AssignmentResponse response = assignmentService.acceptAssignment(id);

        return ResponseEntity.ok(
                ApiResponse.<AssignmentResponse>builder()
                        .success(true)
                        .message("Assignment accepted successfully.")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<AssignmentResponse>> rejectAssignment(
            @PathVariable Long id) {

        AssignmentResponse response = assignmentService.rejectAssignment(id);

        return ResponseEntity.ok(
                ApiResponse.<AssignmentResponse>builder()
                        .success(true)
                        .message("Assignment rejected successfully.")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<ApiResponse<AssignmentResponse>> startTrip(
            @PathVariable Long id) {

        AssignmentResponse response = assignmentService.startTrip(id);

        return ResponseEntity.ok(
                ApiResponse.<AssignmentResponse>builder()
                        .success(true)
                        .message("Trip started successfully.")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}/arrived")
    public ResponseEntity<ApiResponse<AssignmentResponse>> arrived(
            @PathVariable Long id) {

        AssignmentResponse response = assignmentService.arrived(id);

        return ResponseEntity.ok(
                ApiResponse.<AssignmentResponse>builder()
                        .success(true)
                        .message("Partner arrived successfully.")
                        .data(response)
                        .build());
    }    @PutMapping("/{id}/verify-otp")
    public ResponseEntity<ApiResponse<AssignmentResponse>> verifyOtp(
            @PathVariable Long id,
            @Valid @RequestBody VerifyOtpRequest request) {

        AssignmentResponse response =
                assignmentService.verifyOtp(id, request.getOtp());

        return ResponseEntity.ok(
                ApiResponse.<AssignmentResponse>builder()
                        .success(true)
                        .message("OTP verified successfully.")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<AssignmentResponse>> completeAssignment(
            @PathVariable Long id) {

        AssignmentResponse response =
                assignmentService.completeAssignment(id);

        return ResponseEntity.ok(
                ApiResponse.<AssignmentResponse>builder()
                        .success(true)
                        .message("Assignment completed successfully.")
                        .data(response)
                        .build());
    }

}