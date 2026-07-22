package com.washgo.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

}