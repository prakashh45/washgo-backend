package com.washgo.auth.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.washgo.auth.dto.response.UserResponse;
import com.washgo.auth.entity.User;
import com.washgo.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.washgo.common.enums.Role;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/sync")
    public UserResponse syncUser(
            @RequestHeader("Authorization") String authorization
    ) throws Exception {

        String token = authorization.replace("Bearer ", "");

        FirebaseToken firebaseToken =
                FirebaseAuth.getInstance().verifyIdToken(token);

        User user = userService.createOrGetUser(
                firebaseToken.getUid(),
                firebaseToken.getEmail(),
                firebaseToken.getName(),
                null,
                firebaseToken.getPicture()
        );

        return UserResponse.builder()
                .id(user.getId())
                .firebaseUid(user.getFirebaseUid())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .build();
    }

    @GetMapping("/health")
    public String health() {
        return "Auth Service Running";
    }
}