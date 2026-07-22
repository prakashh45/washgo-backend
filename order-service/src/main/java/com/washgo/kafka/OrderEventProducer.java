package com.washgo.kafka;

import com.washgo.config.KafkaTopics;
import com.washgo.common.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, com.washgo.common.event.OrderCreatedEvent> kafkaTemplate;

    public void publishOrderCreated(OrderCreatedEvent  event) {

        log.info("Publishing OrderCreatedEvent : {}", event.getOrderNumber());

        kafkaTemplate.send(
                KafkaTopics.ORDER_CREATED,
                event.getOrderNumber(),
                event
        );
    }
}