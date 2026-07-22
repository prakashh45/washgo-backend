package com.washgo.dto.request;

import com.washgo.enums.AssignmentStatus;
import lombok.Data;

@Data
public class UpdateAssignmentStatusRequest {

    private AssignmentStatus status;

    private String remarks;
}