package com.washgo.common.config;
import com.washgo.common.config.KafkaTopics;

public final class KafkaTopics {

    private KafkaTopics() {}

    public static final String ORDER_CREATED = "order-created";

    public static final String PAYMENT_CREATED = "payment-created";

    public static final String PAYMENT_SUCCESS = "payment-success";

    public static final String PAYMENT_FAILED = "payment-failed";

    public static final String PAYMENT_REFUND = "payment-refund";
}