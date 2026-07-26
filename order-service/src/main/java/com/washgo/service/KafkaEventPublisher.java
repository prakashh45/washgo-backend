package com.washgo.service;

import com.washgo.common.constants.KafkaConstants;
import com.washgo.common.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send(
                KafkaConstants.ORDER_CREATED_TOPIC,
                event.getOrderNumber(),
                event
        );
    }
}