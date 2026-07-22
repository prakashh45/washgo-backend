package com.washgo.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequest {

    private String userId;
    private String recipient;
    private String title;
    private String message;

    private NotificationType type;
}