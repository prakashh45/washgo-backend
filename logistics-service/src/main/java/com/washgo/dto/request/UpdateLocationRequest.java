package com.washgo.dto.request;

import lombok.Data;

@Data
public class UpdateLocationRequest {

    private Double currentLatitude;
    private Double currentLongitude;
}