package com.washgo.controller;

import com.washgo.common.ApiResponse;
import com.washgo.dto.request.AssignDeliveryRequest;
import com.washgo.dto.response.AssignmentResponse;
import com.washgo.service.DeliveryAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/logistics")
@RequiredArgsConstructor
public class LogisticsController {

    private final DeliveryAssignmentService assignmentService;

    @PostMapping("/pickup")
    public ResponseEntity<ApiResponse<AssignmentResponse>> assignPickup(
            @Valid @RequestBody AssignDeliveryRequest request) {

        AssignmentResponse response = assignmentService.assignPartner(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AssignmentResponse>builder()
                        .success(true)
                        .message("Pickup assigned successfully.")
                        .data(response)
                        .build());
    }
}
