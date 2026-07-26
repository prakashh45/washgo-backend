package com.washgo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "authServiceWebClient")
    public WebClient authServiceWebClient(WebClient.Builder builder) {

        return builder
                .baseUrl("http://localhost:8081")
                .build();

        // If using Eureka later:
        // .baseUrl("http://AUTH-SERVICE")
    }
}