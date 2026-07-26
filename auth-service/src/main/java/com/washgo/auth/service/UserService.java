package com.washgo.auth.service;

import com.washgo.common.dto.SyncUserRequest;
import com.washgo.common.dto.SyncUserResponse;
import com.washgo.auth.entity.User;
import com.washgo.common.enums.Role;
import com.washgo.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Find existing user by Firebase UID.
     * If not found, create a new user.
     */
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
                            .email(email == null ? "" : email)
                            .fullName(fullName == null ? "Unknown User" : fullName)
                            .phoneNumber(phoneNumber)
                            .profileImage(profileImage)
                            .role(Role.CUSTOMER)
                            .active(true)
                            .build();

                    return userRepository.save(user);
                });
    }

    /**
     * Synchronize Firebase user with PostgreSQL.
     */
    public SyncUserResponse syncUser(SyncUserRequest request) {

        User user = createOrGetUser(
                request.firebaseUid(),
                request.email(),
                request.fullName(),
                request.phoneNumber(),
                request.profileImage()
        );

        return new SyncUserResponse(
                user.getId(),
                user.getFirebaseUid(),
                user.getRole(),
                user.getActive()
        );
    }
}