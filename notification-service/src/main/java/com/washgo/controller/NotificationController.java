package com.washgo.controller;

import com.washgo.dto.NotificationRequest;
import com.washgo.dto.NotificationResponse;
import com.washgo.entity.NotificationStatus;
import com.washgo.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse sendNotification(
            @Valid @RequestBody NotificationRequest request) {

        return notificationService.sendNotification(request);
    }

    @GetMapping("/{id}")
    public NotificationResponse getNotificationById(@PathVariable Long id) {

        return notificationService.getNotificationById(id);
    }

    @GetMapping("/user/{userId}")
    public List<NotificationResponse> getUserNotifications(
            @PathVariable String userId) {

        return notificationService.getNotificationsByUser(userId);
    }

    @GetMapping
    public List<NotificationResponse> getAllNotifications() {

        return notificationService.getAllNotifications();
    }

    @PatchMapping("/{id}/status")
    public NotificationResponse updateStatus(
            @PathVariable Long id,
            @RequestParam NotificationStatus status) {

        return notificationService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotification(@PathVariable Long id) {

        notificationService.deleteNotification(id);
    }
}