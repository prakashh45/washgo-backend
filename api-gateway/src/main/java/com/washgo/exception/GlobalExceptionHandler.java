package com.washgo.exception;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {
                  "timestamp":"%s",
                  "status":%d,
                  "error":"%s",
                  "message":"%s",
                  "path":"%s"
                }
                """.formatted(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage() == null ? "Unexpected Error" : ex.getMessage(),
                exchange.getRequest().getURI().getPath()
        );

        var buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}