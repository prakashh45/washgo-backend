package com.washgo.repository;

import com.washgo.entity.DeliveryPartner;
import com.washgo.enums.DeliveryPartnerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {

    Optional<DeliveryPartner> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    Optional<DeliveryPartner> findByVehicleNumber(String vehicleNumber);

    List<DeliveryPartner> findByAvailableTrue();

    List<DeliveryPartner> findByStatus(DeliveryPartnerStatus status);

}