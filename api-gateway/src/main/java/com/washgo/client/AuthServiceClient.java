package com.washgo.client;

import com.washgo.common.dto.SyncUserRequest;
import com.washgo.common.dto.SyncUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthServiceClient {

    @Qualifier("authServiceWebClient")
    private final WebClient webClient;

    /**
     * Synchronize Firebase user with Auth Service.
     */
    public Mono<SyncUserResponse> syncUser(
            String firebaseUid,
            String email,
            String fullName,
            String phoneNumber,
            String profileImage
    ) {

        SyncUserRequest request = new SyncUserRequest(
                firebaseUid,
                email,
                fullName,
                phoneNumber,
                profileImage
        );

        return webClient
                .post()
                .uri("/internal/auth/sync")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SyncUserResponse.class);
    }
}