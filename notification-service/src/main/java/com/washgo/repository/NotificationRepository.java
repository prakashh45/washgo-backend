package com.washgo.repository;

import com.washgo.entity.Notification;
import com.washgo.entity.NotificationStatus;
import com.washgo.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(String userId);

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByType(NotificationType type);

    List<Notification> findByUserIdAndStatus(String userId,
                                             NotificationStatus status);
}