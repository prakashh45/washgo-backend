package com.washgo.catalog.controller;

import com.washgo.catalog.dto.request.CreateLaundryServiceRequest;
import com.washgo.catalog.dto.response.LaundryServiceResponse;
import com.washgo.catalog.service.LaundryServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/partners/{partnerId}/services")
@RequiredArgsConstructor
public class LaundryServiceController {

    private final LaundryServiceService laundryServiceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LaundryServiceResponse createService(
            @PathVariable Long partnerId,
            @RequestBody CreateLaundryServiceRequest request) {

        return laundryServiceService.createService(partnerId, request);
    }

    @GetMapping
    public List<LaundryServiceResponse> getPartnerServices(
            @PathVariable Long partnerId) {

        return laundryServiceService.getPartnerServices(partnerId);
    }
}