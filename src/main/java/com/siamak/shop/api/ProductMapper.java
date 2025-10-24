package com.siamak.shop.api;

import com.siamak.shop.client.catalog.ProductDto;
import com.siamak.shop.model.Product;

public class ProductMapper {

    // Convert entity -> DTO
    public static ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getImage_url(),
                product.getPrice(),
                product.getQuantity()
        );
    }

    // Convert DTO -> entity
    public static Product toEntity(ProductDto dto) {
        return Product.builder()
                .id(dto.id())             // use record getter
                .name(dto.name())
                .price(dto.price())
                .description(dto.description())
                .image_url(dto.image_url())
                .quantity(dto.quantity())
                .build();
    }
}