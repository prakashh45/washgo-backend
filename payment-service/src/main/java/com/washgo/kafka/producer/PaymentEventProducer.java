package com.washgo.kafka.producer;

import com.washgo.common.config.KafkaTopics;
import com.washgo.kafka.event.PaymentCreatedEvent;
import com.washgo.kafka.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentCreated(PaymentCreatedEvent event) {
        kafkaTemplate.send(
                KafkaTopics.PAYMENT_CREATED,
                event.getPaymentNumber(),
                event);
    }

    public void publishPaymentSuccess(PaymentSuccessEvent event) {
        kafkaTemplate.send(
                KafkaTopics.PAYMENT_SUCCESS,
                event.getPaymentNumber(),
                event);
    }
}