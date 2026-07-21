package com.washgo.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class LoggingFilter implements WebFilter {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             WebFilterChain chain) {

        long start = System.currentTimeMillis();

        String method =
                exchange.getRequest().getMethod().name();

        String path =
                exchange.getRequest().getURI().getPath();

        String requestId =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst("X-Request-Id");

        log.info("Incoming Request [{}] {} {}",
                requestId,
                method,
                path);

        return chain.filter(exchange)
                .doFinally(signal -> {

                    long time =
                            System.currentTimeMillis() - start;

                    int status =
                            exchange.getResponse()
                                    .getStatusCode() == null
                                    ? 200
                                    : exchange.getResponse()
                                    .getStatusCode()
                                    .value();

                    log.info(
                            "Completed [{}] Status={} Time={}ms",
                            requestId,
                            status,
                            time
                    );
                });
    }
}