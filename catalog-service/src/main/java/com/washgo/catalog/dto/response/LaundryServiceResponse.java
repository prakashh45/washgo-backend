package com.washgo.catalog.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LaundryServiceResponse {

    private Long id;

    private String serviceName;

    private String description;

    private Boolean active;
}