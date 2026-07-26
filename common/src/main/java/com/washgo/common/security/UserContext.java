package com.washgo.common.security;

import com.washgo.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {

    private UUID userId;

    private String firebaseUid;

    private Role role;
}