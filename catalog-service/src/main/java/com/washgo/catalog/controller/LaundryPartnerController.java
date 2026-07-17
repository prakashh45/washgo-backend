package com.washgo.catalog.controller;

import com.washgo.catalog.dto.request.CreateLaundryPartnerRequest;
import com.washgo.catalog.dto.response.LaundryPartnerResponse;
import com.washgo.catalog.service.LaundryPartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/partners")
@RequiredArgsConstructor
public class LaundryPartnerController {

    private final LaundryPartnerService laundryPartnerService;

    /**
     * Create a new Laundry Partner
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LaundryPartnerResponse createPartner(
            @RequestBody CreateLaundryPartnerRequest request) {

        return laundryPartnerService.createPartner(request);
    }

    /**
     * Get all Laundry Partners
     */
    @GetMapping
    public List<LaundryPartnerResponse> getAllPartners() {
        return laundryPartnerService.getAllPartners();
    }

    /**
     * Get Laundry Partner by ID
     */
    @GetMapping("/{id}")
    public LaundryPartnerResponse getPartnerById(@PathVariable Long id) {
        return laundryPartnerService.getPartnerById(id);
    }
}