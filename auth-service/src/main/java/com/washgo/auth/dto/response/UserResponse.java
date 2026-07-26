package com.washgo.auth.dto.response;

import com.washgo.common.enums.Role;
import lombok.*;

import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private  String firebaseUid;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String profileImage;
    private Role role;
}
