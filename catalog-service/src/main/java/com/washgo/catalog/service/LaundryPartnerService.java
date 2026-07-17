package com.washgo.catalog.service;

import com.washgo.catalog.dto.request.CreateLaundryPartnerRequest;
import com.washgo.catalog.dto.response.LaundryPartnerResponse;

import java.util.List;

public interface LaundryPartnerService {

    LaundryPartnerResponse createPartner(CreateLaundryPartnerRequest request);

    List<LaundryPartnerResponse> getAllPartners();

    LaundryPartnerResponse getPartnerById(Long id);
}