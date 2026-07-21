package com.washgo.service;

import com.washgo.dto.NotificationRequest;
import com.washgo.dto.NotificationResponse;
import com.washgo.entity.Notification;
import com.washgo.entity.NotificationStatus;
import com.washgo.entity.NotificationType;
import com.washgo.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final EmailService emailService;

    @Override
    public NotificationResponse sendNotification(NotificationRequest request) {

        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .recipient(request.getRecipient())
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .build();

        try {

            if (request.getType() == NotificationType.EMAIL) {
                emailService.sendEmail(
                        request.getRecipient(),
                        request.getTitle(),
                        request.getMessage()
                );
            }

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());

        } catch (Exception ex) {

            notification.setStatus(NotificationStatus.FAILED);
        }

        Notification saved = repository.save(notification);

        return map(saved);
    }

    @Override
    public NotificationResponse getNotificationById(Long id) {

        Notification notification = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Notification not found"));

        return map(notification);
    }

    @Override
    public List<NotificationResponse> getNotificationsByUser(String userId) {

        return repository.findByUserId(userId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public List<NotificationResponse> getAllNotifications() {

        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public NotificationResponse updateStatus(Long id,
                                             NotificationStatus status) {

        Notification notification = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Notification not found"));

        notification.setStatus(status);

        if (status == NotificationStatus.SENT) {
            notification.setSentAt(LocalDateTime.now());
        }

        return map(repository.save(notification));
    }

    @Override
    public void deleteNotification(Long id) {

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Notification not found");
        }

        repository.deleteById(id);
    }


    private NotificationResponse map(Notification notification) {

        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .recipient(notification.getRecipient())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .build();
    }
}