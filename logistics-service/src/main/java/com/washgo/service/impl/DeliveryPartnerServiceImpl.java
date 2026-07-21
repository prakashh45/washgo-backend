package com.washgo.service.impl;

import com.washgo.dto.request.CreateDeliveryPartnerRequest;
import com.washgo.dto.request.UpdateAvailabilityRequest;
import com.washgo.dto.request.UpdateDeliveryPartnerRequest;
import com.washgo.dto.request.UpdateLocationRequest;
import com.washgo.dto.request.UpdateStatusRequest;
import com.washgo.dto.response.DeliveryPartnerResponse;
import com.washgo.entity.DeliveryPartner;
import com.washgo.enums.DeliveryPartnerStatus;
import com.washgo.repository.DeliveryPartnerRepository;
import com.washgo.service.DeliveryPartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryPartnerServiceImpl implements DeliveryPartnerService {

    private final DeliveryPartnerRepository repository;

    @Override
    public DeliveryPartnerResponse createPartner(CreateDeliveryPartnerRequest request) {

        if (repository.existsByUserId(request.getUserId())) {
            throw new RuntimeException("Delivery Partner already exists.");
        }

        if (repository.findByVehicleNumber(request.getVehicleNumber()).isPresent()) {
            throw new RuntimeException("Vehicle number already registered.");
        }

        DeliveryPartner partner = DeliveryPartner.builder()
                .userId(request.getUserId())
                .vehicleType(request.getVehicleType())
                .vehicleNumber(request.getVehicleNumber())
                .drivingLicense(request.getDrivingLicense())
                .aadhaarNumber(request.getAadhaarNumber())
                .currentLatitude(null)
                .currentLongitude(null)
                .rating(0.0)
                .totalDeliveries(0)
                .available(true)
                .verified(false)
                .status(DeliveryPartnerStatus.ACTIVE)
                .build();

        DeliveryPartner savedPartner = repository.save(partner);

        return mapToResponse(savedPartner);
    }

    @Override
    public DeliveryPartnerResponse getPartnerById(Long id) {

        DeliveryPartner partner = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Delivery Partner not found"));

        return mapToResponse(partner);
    }

    @Override
    public List<DeliveryPartnerResponse> getAllPartners() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public DeliveryPartnerResponse updatePartner(Long id,
                                                 UpdateDeliveryPartnerRequest request) {

        DeliveryPartner partner = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery Partner not found"));

        partner.setVehicleType(request.getVehicleType());
        partner.setVehicleNumber(request.getVehicleNumber());
        partner.setDrivingLicense(request.getDrivingLicense());
        partner.setAadhaarNumber(request.getAadhaarNumber());

        DeliveryPartner updatedPartner = repository.save(partner);

        return mapToResponse(updatedPartner);
    }
    @Override
    public void deletePartner(Long id) {

    }

    @Override
    public DeliveryPartnerResponse updateAvailability(Long id,
                                                      UpdateAvailabilityRequest request) {

        DeliveryPartner partner = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery Partner not found"));

        partner.setAvailable(request.getAvailable());

        DeliveryPartner updatedPartner = repository.save(partner);

        return mapToResponse(updatedPartner);
    }
    @Override
    public DeliveryPartnerResponse updateLocation(Long id,
                                                  UpdateLocationRequest request) {

        DeliveryPartner partner = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery Partner not found"));

        partner.setCurrentLatitude(request.getCurrentLatitude());
        partner.setCurrentLongitude(request.getCurrentLongitude());

        DeliveryPartner updatedPartner = repository.save(partner);

        return mapToResponse(updatedPartner);
    }

    @Override
    public DeliveryPartnerResponse updateStatus(Long id, UpdateStatusRequest request) {
        return null;
    }

    private DeliveryPartnerResponse mapToResponse(DeliveryPartner partner) {

        DeliveryPartnerResponse response = new DeliveryPartnerResponse();

        response.setId(partner.getId());
        response.setUserId(partner.getUserId());
        response.setVehicleType(partner.getVehicleType());
        response.setVehicleNumber(partner.getVehicleNumber());
        response.setDrivingLicense(partner.getDrivingLicense());
        response.setAadhaarNumber(partner.getAadhaarNumber());
        response.setCurrentLatitude(partner.getCurrentLatitude());
        response.setCurrentLongitude(partner.getCurrentLongitude());
        response.setRating(partner.getRating());
        response.setTotalDeliveries(partner.getTotalDeliveries());
        response.setAvailable(partner.getAvailable());
        response.setVerified(partner.getVerified());
        response.setStatus(partner.getStatus());

        return response;
    }
}