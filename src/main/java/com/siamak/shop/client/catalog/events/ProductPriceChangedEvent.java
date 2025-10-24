package com.siamak.shop.client.catalog.events;

import java.math.BigDecimal;

public record ProductPriceChangedEvent (
    String eventId,          // UUID as string
    String eventType,        // "catalog.product-price-changed"
    String occurredAt,       // ISO-8601
    Long productId,
    String name,
    BigDecimal price
) {}
