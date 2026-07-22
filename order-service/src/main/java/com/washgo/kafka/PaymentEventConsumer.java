package com.washgo.kafka;

import com.washgo.common.event.PaymentSuccessEvent;
import com.washgo.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.washgo.common.config.KafkaTopics;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_SUCCESS,
            groupId = "order-service")
    public void consume(PaymentSuccessEvent event) {

        log.info("Payment received for order {}", event.getOrderId());

        orderService.markPaymentSuccess(
                event.getOrderId(),
                event.getPaymentNumber());
    }
}