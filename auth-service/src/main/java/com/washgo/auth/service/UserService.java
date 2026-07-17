package com.washgo.auth.service;

import com.washgo.auth.entity.User;
import com.washgo.auth.enums.Role;
import com.washgo.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createOrGetUser(
            String firebaseUid,
            String email,
            String fullName,
            String phoneNumber,
            String profileImage
    ) {

        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseGet(() -> {

                    User user = User.builder()
                            .firebaseUid(firebaseUid)
                            .email(email)
                            .fullName(fullName)
                            .phoneNumber(phoneNumber)
                            .profileImage(profileImage)
                            .role(Role.CUSTOMER)
                            .active(true)
                            .build();

                    return userRepository.save(user);
                });
    }
}