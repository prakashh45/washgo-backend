package com.washgo.filter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
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

import java.util.List;

@Component
public class FirebaseAuthenticationFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Public Endpoints
        if (path.startsWith("/api/v1/auth") || path.startsWith("/actuator")) {
            return chain.filter(exchange);
        }

        // Read Authorization Header
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String idToken = authHeader.substring(7);

        try {

            // Verify Firebase Token
            FirebaseToken firebaseToken =
                    FirebaseAuth.getInstance().verifyIdToken(idToken);

            // Read Role from Firebase Custom Claims
            Object claim = firebaseToken.getClaims().get("role");

            final String role = (claim != null)
                    ? claim.toString().toUpperCase()
                    : "CUSTOMER";

            // Create Authorities
            List<SimpleGrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));

            // Create Spring Authentication
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            firebaseToken.getUid(),
                            null,
                            authorities
                    );

            // Forward User Information
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(builder -> builder
                            .header("X-User-Id", firebaseToken.getUid())
                            .header("X-User-Email",
                                    firebaseToken.getEmail() == null
                                            ? ""
                                            : firebaseToken.getEmail())
                              .header("X-User-Role", role)
                    )
                    .build();

            return chain.filter(mutatedExchange)
                    .contextWrite(
                            ReactiveSecurityContextHolder.withAuthentication(authentication)
                    );

        } catch (Exception e) {

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}