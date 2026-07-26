package com.washgo.auth.controller;

import com.washgo.common.dto.SyncUserRequest;
import com.washgo.common.dto.SyncUserResponse;
import com.washgo.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @PostMapping("/sync")
    public SyncUserResponse syncUser(
            @Valid @RequestBody SyncUserRequest request
    ) {
        return userService.syncUser(request);
    }
}