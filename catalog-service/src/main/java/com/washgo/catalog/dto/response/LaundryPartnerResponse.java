package com.washgo.catalog.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LaundryPartnerResponse {

    private Long id;

    private String shopName;
    private String ownerName;
    private String phoneNumber;
    private String email;

    private String address;
    private String city;

    private Double latitude;
    private Double longitude;

    private String coverImage;

    private Boolean verified;
    private Boolean holidayMode;

    private Double averageRating;
    private Integer totalReviews;

    private Boolean active;
}