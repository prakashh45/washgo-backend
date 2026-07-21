package com.washgo.dto.request;

import jakarta.validation.constraints.NotNull;

public class CompleteAssignmentRequest {

    @NotNull(message = "Assignment Id is required")
    private Long assignmentId;

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }
}