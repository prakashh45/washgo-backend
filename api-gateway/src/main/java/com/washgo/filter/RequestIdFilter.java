package com.washgo.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter implements WebFilter {

    public static final String REQUEST_ID = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             WebFilterChain chain) {

        String requestId = UUID.randomUUID().toString();

        ServerWebExchange mutatedExchange =
                exchange.mutate()
                        .request(builder ->
                                builder.header(REQUEST_ID, requestId))
                        .build();

        mutatedExchange.getResponse()
                .getHeaders()
                .add(REQUEST_ID, requestId);

        return chain.filter(mutatedExchange);
    }
}