package com.washgo.entity;

import java.time.LocalDateTime;

public abstract class BaseEntity {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}