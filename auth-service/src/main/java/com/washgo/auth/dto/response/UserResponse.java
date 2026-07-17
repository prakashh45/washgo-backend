package com.washgo.auth.dto.response;

import com.washgo.auth.enums.Role;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private  Long id ;
    private  String firebaseUid;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String profileImage;
    private Role role;
}
