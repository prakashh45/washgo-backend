package com.washgo.service;

import com.washgo.dto.NotificationRequest;
import com.washgo.dto.NotificationResponse;
import com.washgo.entity.NotificationStatus;

import java.util.List;

public interface NotificationService {

    NotificationResponse sendNotification(NotificationRequest request);

    NotificationResponse getNotificationById(Long id);

    List<NotificationResponse> getNotificationsByUser(String userId);

    List<NotificationResponse> getAllNotifications();

    NotificationResponse updateStatus(Long id, NotificationStatus status);

    void deleteNotification(Long id);
}