package com.washgo.kafka;

import com.washgo.common.constants.KafkaConstants;
import com.washgo.common.event.OrderCreatedEvent;
import com.washgo.entity.ProcessedEvent;
import com.washgo.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(
            topics = KafkaConstants.ORDER_CREATED_TOPIC,
            groupId = KafkaConstants.NOTIFICATION_GROUP
    )
    public void consume(OrderCreatedEvent event) {

        // Idempotency Check
        if (processedEventRepository.existsById(event.getEventId())) {
            log.info("Duplicate event ignored : {}", event.getEventId());
            return;
        }

        log.info("======================================");
        log.info("Order Created Event Received");
        log.info("Event ID      : {}", event.getEventId());
        log.info("Order Number  : {}", event.getOrderNumber());
        log.info("Customer ID   : {}", event.getCustomerId());
        log.info("Amount        : {}", event.getTotalAmount());
        log.info("======================================");

        // TODO:
        // Save notification to database
        // Send email
        // Send push notification

        // Mark event as processed
        processedEventRepository.save(
                ProcessedEvent.builder()
                        .eventId(event.getEventId())
                        .processedAt(LocalDateTime.now())
                        .build()
        );

        log.info("Event processed successfully : {}", event.getEventId());
    }
}