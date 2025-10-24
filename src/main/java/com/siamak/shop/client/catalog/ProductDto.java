package com.siamak.shop.client.catalog;

import java.math.BigDecimal;

public record ProductDto (
        Long id,
        String name,
        String description,
        String image_url,
        BigDecimal price,
        Integer quantity
) { }
