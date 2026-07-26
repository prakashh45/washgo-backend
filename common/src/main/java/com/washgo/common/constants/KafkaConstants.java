package com.washgo.common.constants;

public final class KafkaConstants {

    private KafkaConstants() {}

    public static final String ORDER_CREATED_TOPIC = "order-created";
    public static final String ORDER_ACCEPTED_TOPIC = "order-accepted";
    public static final String PAYMENT_COMPLETED_TOPIC = "payment-completed";
    public static final String ORDER_DELIVERED_TOPIC = "order-delivered";
    public static final String ORDER_CREATED_DLT_TOPIC = "order-created-dlt";

    public static final String ORDER_GROUP = "order-group";
    public static final String NOTIFICATION_GROUP = "notification-group";
    public static final String LOGISTICS_GROUP = "logistics-group";
    public static final String PAYMENT_GROUP = "payment-group";
}