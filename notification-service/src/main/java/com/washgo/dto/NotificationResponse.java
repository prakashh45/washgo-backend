package com.washgo.dto;

import com.washgo.entity.NotificationStatus;
import com.washgo.entity.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {

    private Long id;

    private String userId;

    private String recipient;

    private String title;

    private String message;

    private NotificationType type;

    private NotificationStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime sentAt;
}