package com.washgo.kafka;

import com.washgo.common.constants.KafkaConstants;
import com.washgo.common.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderCreatedConsumer {

    @KafkaListener(
            topics = KafkaConstants.ORDER_CREATED_TOPIC,
            groupId = KafkaConstants.LOGISTICS_GROUP
    )
    public void consume(OrderCreatedEvent event) {

        log.info("======================================");
        log.info("NEW ORDER RECEIVED FOR LOGISTICS");
        log.info("Order Number : {}", event.getOrderNumber());
        log.info("Customer ID  : {}", event.getCustomerId());
        log.info("Partner ID   : {}", event.getLaundryPartnerId());
        log.info("Pickup Addr  : {}", event.getPickupAddress());
        log.info("======================================");

        // Next phase:
        // Assign pickup partner
        // Create logistics task
        // Publish PickupAssignedEvent
    }
}