package com.washgo.catalog.service;

import com.washgo.catalog.dto.request.CreateLaundryServiceRequest;
import com.washgo.catalog.dto.response.LaundryServiceResponse;

import java.util.List;

public interface LaundryServiceService {

    LaundryServiceResponse createService(Long partnerId,
                                         CreateLaundryServiceRequest request);

    List<LaundryServiceResponse> getPartnerServices(Long partnerId);

}