package com.siamak.shop.client.catalog;

import com.siamak.shop.client.catalog.events.ProductPriceChangedEvent;
import com.siamak.shop.model.Product;
import com.siamak.shop.outbox.OutboxService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CatalogService {
    private final OutboxService outbox;

    @Transactional
    public Product updatePrice(Product p, BigDecimal newPrice) {
        p.setPrice(newPrice);

        var evt = new ProductPriceChangedEvent(
                UUID.randomUUID().toString(),
                "catalog.product-price-changed",
                OffsetDateTime.now(ZoneOffset.UTC).toString(),
                p.getId(),
                p.getName(),
                p.getPrice()
        );

        outbox.add(
                "catalog.product",
                String.valueOf(p.getId()),
                evt.eventType(),
                evt
        );
        return p;
    }
}
