package com.washgo.exception;

public class DeliveryPartnerNotFoundException extends RuntimeException {

    public DeliveryPartnerNotFoundException(String message) {
        super(message);
    }
}