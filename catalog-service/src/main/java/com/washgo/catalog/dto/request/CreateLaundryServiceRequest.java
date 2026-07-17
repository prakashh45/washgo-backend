package com.washgo.catalog.dto.request;

import lombok.Data;

@Data
public class CreateLaundryServiceRequest {

    private String serviceName;

    private String description;
}