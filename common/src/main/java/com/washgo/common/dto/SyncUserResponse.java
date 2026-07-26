package com.washgo.common.dto;

import com.washgo.common.enums.Role;

import java.util.UUID;

public record SyncUserResponse(

        UUID userId,

        String firebaseUid,

        Role role,

        Boolean active
) {
}