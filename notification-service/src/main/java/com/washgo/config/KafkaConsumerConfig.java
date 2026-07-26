package com.washgo.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConsumerConfig {

    /*
     * Spring Boot auto-configures Kafka consumer
     * using spring.kafka.consumer.* properties.
     *
     * We'll add custom configuration later
     * (Retry, DLT, ErrorHandler, etc.).
     */
}