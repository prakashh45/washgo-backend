package com.washgo.controller;
import java.util.List;

import com.washgo.dto.request.*;
import com.washgo.dto.response.DeliveryPartnerResponse;
import com.washgo.service.DeliveryPartnerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/delivery-partners")
@RequiredArgsConstructor
public class DeliveryPartnerController {

    private final DeliveryPartnerService deliveryPartnerService;

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryPartnerResponse> getPartnerById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryPartnerService.getPartnerById(id));
    }

    @GetMapping
    public ResponseEntity<List<DeliveryPartnerResponse>> getAllPartners() {
        return ResponseEntity.ok(deliveryPartnerService.getAllPartners());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryPartnerResponse> updatePartner(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDeliveryPartnerRequest request) {

        return ResponseEntity.ok(deliveryPartnerService.updatePartner(id, request));
    }
    @PatchMapping("/{id}/availability")
    public ResponseEntity<DeliveryPartnerResponse> updateAvailability(
            @PathVariable Long id,
            @RequestBody UpdateAvailabilityRequest request) {

        return ResponseEntity.ok(
                deliveryPartnerService.updateAvailability(id, request));
    }
    @PatchMapping("/{id}/location")
    public ResponseEntity<DeliveryPartnerResponse> updateLocation(
            @PathVariable Long id,
            @RequestBody UpdateLocationRequest request) {

        return ResponseEntity.ok(
                deliveryPartnerService.updateLocation(id, request));
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<DeliveryPartnerResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request) {

        return ResponseEntity.ok(
                deliveryPartnerService.updateStatus(id, request));
    }
    @PostMapping
    public ResponseEntity<DeliveryPartnerResponse> createPartner(
            @Valid @RequestBody CreateDeliveryPartnerRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deliveryPartnerService.createPartner(request));
    }
}