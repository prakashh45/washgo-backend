package com.washgo.security;

import com.washgo.common.enums.Role;
import com.washgo.common.security.GatewayConstants;
import com.washgo.common.security.UserContext;
import com.washgo.common.security.UserContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    @Value("${washgo.gateway.secret}")
    private String gatewaySecret;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {

            String path = request.getRequestURI();

            if (path.startsWith("/actuator")
                    || path.contains("/health")) {

                filterChain.doFilter(request, response);
                return;
            }

            String requestSecret =
                    request.getHeader(GatewayConstants.GATEWAY_SECRET_HEADER);

            if (requestSecret == null || !requestSecret.equals(gatewaySecret)) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Invalid Gateway Key");
                return;
            }

            String userId =
                    request.getHeader(GatewayConstants.USER_ID_HEADER);

            String firebaseUid =
                    request.getHeader(GatewayConstants.FIREBASE_UID_HEADER);

            String role =
                    request.getHeader(GatewayConstants.USER_ROLE_HEADER);

            if (userId == null || firebaseUid == null || role == null) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Missing Authentication Headers");
                return;
            }

            UserContextHolder.setContext(
                    UserContext.builder()
                            .userId(UUID.fromString(userId))
                            .firebaseUid(firebaseUid)
                            .role(Role.valueOf(role))
                            .build()
            );

            filterChain.doFilter(request, response);

        } finally {
            UserContextHolder.clear();
        }
    }
}