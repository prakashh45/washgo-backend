package com.washgo.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.washgo.common.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-created", groupId = "logistics-group")
    public void consume(String message) throws Exception {

        OrderCreatedEvent event =
                objectMapper.readValue(message, OrderCreatedEvent.class);

        log.info("Order Number : {}", event.getOrderNumber());
    }
}