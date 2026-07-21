package com.washgo.service;
import com.washgo.dto.request.*;
import com.washgo.dto.request.CreateDeliveryPartnerRequest;
import com.washgo.dto.request.UpdateAvailabilityRequest;
import com.washgo.dto.request.UpdateDeliveryPartnerRequest;
import com.washgo.dto.request.UpdateLocationRequest;
import com.washgo.dto.response.DeliveryPartnerResponse;

import java.util.List;

public interface DeliveryPartnerService {

    DeliveryPartnerResponse createPartner(CreateDeliveryPartnerRequest request);

    DeliveryPartnerResponse getPartnerById(Long id);

    List<DeliveryPartnerResponse> getAllPartners();

    DeliveryPartnerResponse updatePartner(Long id,
                                          UpdateDeliveryPartnerRequest request);

    void deletePartner(Long id);

    DeliveryPartnerResponse updateAvailability(Long id,
                                               UpdateAvailabilityRequest request);

    DeliveryPartnerResponse updateLocation(Long id,
                                           UpdateLocationRequest request);

    DeliveryPartnerResponse updateStatus(Long id,
                                         UpdateStatusRequest request);



}