package com.washgo.catalog.service.impl;

import com.washgo.catalog.dto.request.CreateLaundryServiceRequest;
import com.washgo.catalog.dto.response.LaundryServiceResponse;
import com.washgo.catalog.entity.LaundryPartner;
import com.washgo.catalog.entity.LaundryService;
import com.washgo.catalog.repository.LaundryPartnerRepository;
import com.washgo.catalog.repository.LaundryServiceRepository;
import com.washgo.catalog.service.LaundryServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaundryServiceServiceImpl implements LaundryServiceService {

    private final LaundryServiceRepository serviceRepository;
    private final LaundryPartnerRepository partnerRepository;

    @Override
    public LaundryServiceResponse createService(Long partnerId,
                                                CreateLaundryServiceRequest request) {

        LaundryPartner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Laundry Partner not found"));

        LaundryService service = LaundryService.builder()
                .partner(partner)
                .serviceName(request.getServiceName())
                .description(request.getDescription())
                .build();

        LaundryService saved = serviceRepository.save(service);

        return map(saved);
    }

    @Override
    public List<LaundryServiceResponse> getPartnerServices(Long partnerId) {

        LaundryPartner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Laundry Partner not found"));

        return serviceRepository.findByPartner(partner)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private LaundryServiceResponse map(LaundryService service) {

        return LaundryServiceResponse.builder()
                .id(service.getId())
                .serviceName(service.getServiceName())
                .description(service.getDescription())
                .active(service.getActive())
                .build();
    }
}