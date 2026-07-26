package com.washgo.security;

import com.washgo.common.enums.Role;

import java.util.UUID;

public class UserContext {
    UUID userId;
    String firebaseUid;
    Role role;
}
