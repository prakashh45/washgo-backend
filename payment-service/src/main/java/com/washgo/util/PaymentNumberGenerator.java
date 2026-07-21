package com.washgo.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentNumberGenerator {

    private static final AtomicLong COUNTER = new AtomicLong(1);

    private PaymentNumberGenerator() {
    }

    public static String generatePaymentNumber() {

        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        long sequence = COUNTER.getAndIncrement();

        return String.format("PAY-%s-%04d", date, sequence);
    }
}