package com.washgo.filter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.washgo.client.AuthServiceClient;
import com.washgo.common.dto.SyncUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import com.washgo.common.security.GatewayConstants;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FirebaseAuthenticationFilter implements WebFilter {

    private final AuthServiceClient authServiceClient;
    @Value("${washgo.gateway.secret}")
    private String gatewaySecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Public endpoints
        if (path.startsWith("/api/v1/auth")
                || path.startsWith("/actuator")
                || path.equals("/api/v1/orders/health")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String idToken = authHeader.substring(7);

        final FirebaseToken firebaseToken;

        try {
            firebaseToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return authServiceClient.syncUser(
                        firebaseToken.getUid(),
                        firebaseToken.getEmail(),
                        firebaseToken.getName(),
                       null,
                        firebaseToken.getPicture()
                )
                .flatMap((SyncUserResponse user) -> {

                    String role = user.role().name();

                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + role)
                    );

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user.userId().toString(),
                                    null,
                                    authorities
                            );

                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(builder -> builder
                                    .header(
                                            GatewayConstants.GATEWAY_SECRET_HEADER,
                                            gatewaySecret
                                    )
                                    .header(
                                            GatewayConstants.USER_ID_HEADER,
                                            user.userId().toString()
                                    )
                                    .header(
                                            GatewayConstants.FIREBASE_UID_HEADER,
                                            user.firebaseUid()
                                    )
                                    .header(
                                            GatewayConstants.USER_ROLE_HEADER,
                                            role
                                    )
                            )
                            .build();

                    return chain.filter(mutatedExchange)
                            .contextWrite(
                                    ReactiveSecurityContextHolder.withAuthentication(authentication)
                            );
                })
                .onErrorResume(ex -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }
}