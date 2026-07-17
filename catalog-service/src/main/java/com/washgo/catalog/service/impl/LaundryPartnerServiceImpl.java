package com.washgo.catalog.service.impl;

import com.washgo.catalog.dto.request.CreateLaundryPartnerRequest;
import com.washgo.catalog.dto.response.LaundryPartnerResponse;
import com.washgo.catalog.entity.LaundryPartner;
import com.washgo.catalog.repository.LaundryPartnerRepository;
import com.washgo.catalog.service.LaundryPartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaundryPartnerServiceImpl implements LaundryPartnerService {

    private final LaundryPartnerRepository repository;

    @Override
    public LaundryPartnerResponse createPartner(CreateLaundryPartnerRequest request) {

        LaundryPartner partner = LaundryPartner.builder()
                .shopName(request.getShopName())
                .ownerName(request.getOwnerName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .address(request.getAddress())
                .city(request.getCity())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .coverImage(request.getCoverImage())
                .build();

        LaundryPartner savedPartner = repository.save(partner);

        return mapToResponse(savedPartner);
    }

    @Override
    public List<LaundryPartnerResponse> getAllPartners() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LaundryPartnerResponse getPartnerById(Long id) {

        LaundryPartner partner = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Laundry Partner not found"));

        return mapToResponse(partner);
    }

    private LaundryPartnerResponse mapToResponse(LaundryPartner partner) {

        return LaundryPartnerResponse.builder()
                .id(partner.getId())
                .shopName(partner.getShopName())
                .ownerName(partner.getOwnerName())
                .phoneNumber(partner.getPhoneNumber())
                .email(partner.getEmail())
                .address(partner.getAddress())
                .city(partner.getCity())
                .latitude(partner.getLatitude())
                .longitude(partner.getLongitude())
                .coverImage(partner.getCoverImage())
                .verified(partner.getVerified())
                .holidayMode(partner.getHolidayMode())
                .averageRating(partner.getAverageRating())
                .totalReviews(partner.getTotalReviews())
                .active(partner.getActive())
                .build();
    }
}