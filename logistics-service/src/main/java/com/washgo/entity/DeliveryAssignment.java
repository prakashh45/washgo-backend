package com.washgo.entity;

import com.washgo.enums.AssignmentStatus;
import com.washgo.enums.DeliveryLegType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_assignments")
public class DeliveryAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_partner_id", nullable = false)
    private DeliveryPartner deliveryPartner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryLegType legType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    @Column(length = 6)
    private String pickupOtp;

    @Column(length = 6)
    private String deliveryOtp;

    private String pickupPhotoUrl;

    private String deliveryPhotoUrl;

    private LocalDateTime assignedAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime startedAt;

    private LocalDateTime arrivedAt;

    private LocalDateTime completedAt;

    @Column(length = 500)
    private String remarks;

    public DeliveryAssignment() {
    }

    @PrePersist
    public void prePersist() {
        assignedAt = LocalDateTime.now();
        if (status == null) {
            status = AssignmentStatus.ASSIGNED;
        }
    }

    // -------- Getters & Setters --------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public DeliveryPartner getDeliveryPartner() {
        return deliveryPartner;
    }

    public String getPartnerName() {
        return deliveryPartner.getDeliveryPartnerName();
    }

    public void setDeliveryPartner(DeliveryPartner deliveryPartner) {
        this.deliveryPartner = deliveryPartner;
    }

    public DeliveryLegType getLegType() {
        return legType;
    }

    public void setLegType(DeliveryLegType legType) {
        this.legType = legType;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public String getPickupOtp() {
        return pickupOtp;
    }

    public void setPickupOtp(String pickupOtp) {
        this.pickupOtp = pickupOtp;
    }

    public String getDeliveryOtp() {
        return deliveryOtp;
    }

    public void setDeliveryOtp(String deliveryOtp) {
        this.deliveryOtp = deliveryOtp;
    }

    public String getPickupPhotoUrl() {
        return pickupPhotoUrl;
    }

    public void setPickupPhotoUrl(String pickupPhotoUrl) {
        this.pickupPhotoUrl = pickupPhotoUrl;
    }

    public String getDeliveryPhotoUrl() {
        return deliveryPhotoUrl;
    }

    public void setDeliveryPhotoUrl(String deliveryPhotoUrl) {
        this.deliveryPhotoUrl = deliveryPhotoUrl;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getArrivedAt() {
        return arrivedAt;
    }

    public void setArrivedAt(LocalDateTime arrivedAt) {
        this.arrivedAt = arrivedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}