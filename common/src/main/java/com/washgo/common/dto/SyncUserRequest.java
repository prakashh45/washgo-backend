package com.washgo.common.dto;

public record SyncUserRequest(

        String firebaseUid,

        String email,

        String fullName,

        String phoneNumber,

        String profileImage
) {
}