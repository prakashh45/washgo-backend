package com.washgo.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class LoggingFilter implements WebFilter {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             WebFilterChain chain) {

        long start = System.currentTimeMillis();

        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();

        String requestId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Request-Id");

        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        String userId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-User-Id");

        String role = exchange.getRequest()
                .getHeaders()
                .getFirst("X-User-Role");

        log.info("Incoming Request | RequestId={} | Method={} | Path={}",
                requestId,
                method,
                path);

        final String finalRequestId = requestId;

        return chain.filter(exchange)
                .doFinally(signal -> {

                    long time = System.currentTimeMillis() - start;

                    HttpStatusCode statusCode =
                            exchange.getResponse().getStatusCode();

                    int status = statusCode != null
                            ? statusCode.value()
                            : 200;

                    log.info(
                            "Completed Request | RequestId={} | User={} | Role={} | Status={} | Time={}ms",
                            finalRequestId,
                            userId == null ? "Anonymous" : userId,
                            role == null ? "N/A" : role,
                            status,
                            time
                    );
                });
    }
}