package com.washgo.catalog.dto.request;

import lombok.Data;

@Data
public class CreateLaundryPartnerRequest {

    private String shopName;
    private String ownerName;
    private String phoneNumber;
    private String email;
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    private String coverImage;
}