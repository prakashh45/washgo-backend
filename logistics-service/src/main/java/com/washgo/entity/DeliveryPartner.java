package com.washgo.entity;

import com.washgo.enums.DeliveryPartnerStatus;
import com.washgo.enums.DeliveryPartnerStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "delivery_partners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deliveryPatnerName;

    private Long userId;

    private String vehicleType;

    @Column(unique = true)
    private String vehicleNumber;

    private String drivingLicense;

    private String aadhaarNumber;

    private Double currentLatitude;

    private Double currentLongitude;

    private Double rating;

    private Integer totalDeliveries;

    private Boolean available;

    private Boolean verified;



    @Enumerated(EnumType.STRING)
    private DeliveryPartnerStatus status;

}