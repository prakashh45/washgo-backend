package com.washgo.repository;

import com.washgo.entity.DeliveryAssignment;
import com.washgo.enums.AssignmentStatus;
import com.washgo.enums.DeliveryLegType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryAssignmentRepository extends JpaRepository<DeliveryAssignment, Long> {

    List<DeliveryAssignment> findByOrderId(Long orderId);

    List<DeliveryAssignment> findByDeliveryPartnerId(Long deliveryPartnerId);

    List<DeliveryAssignment> findByStatus(AssignmentStatus status);

    List<DeliveryAssignment> findByLegType(DeliveryLegType legType);

    Optional<DeliveryAssignment> findByOrderIdAndLegType(
            Long orderId,
            DeliveryLegType legType);

    boolean existsByOrderIdAndLegType(
            Long orderId,
            DeliveryLegType legType);
}