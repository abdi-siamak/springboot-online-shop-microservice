package com.siamak.shop.client.user.events;

public record UserPasswordChangedEvent (
    String eventId,          // UUID as string
    String eventType,        // "user.password-changed"
    String occurredAt,       // ISO-8601
    Long userId,
    String name,
    String password
){}
