package com.siamak.shop.client.catalog;

import com.siamak.shop.api.ProductMapper;
import com.siamak.shop.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CatalogClient {
    private final WebClient webClient;
    private final CatalogService catalogService;
    private static final Logger log = LoggerFactory.getLogger(CatalogClient.class);

    public CatalogClient(@Value("${catalog.service.url}") String catalogServiceUrl, CatalogService catalogService) {
        this.catalogService = catalogService;
        this.webClient = WebClient.builder()
                .baseUrl(catalogServiceUrl)
                .build();
    }

    public List<Product> getAllProducts() {
        return webClient.get()
                .uri("/products")
                .retrieve()
                .bodyToFlux(Product.class)
                .collectList()
                .block();
    }

    public Product getProductById(Long id) {
        return webClient.get()
                .uri("/products/{id}", id)
                .retrieve()
                .bodyToMono(Product.class)
                .onErrorResume(e -> Mono.empty())
                .block();
    }

    public Product addProduct(Product product) {
        // Convert entity -> DTO before sending
        ProductDto dtoToSend = ProductMapper.toDto(product);
        log.info("Sending product: {}", product);
        // Send DTO and receive DTO response
        ProductDto responseDto = webClient.post()
                .uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dtoToSend)
                .retrieve()
                .bodyToMono(ProductDto.class)
                .block();

        // Convert received DTO -> entity
        return ProductMapper.toEntity(responseDto);
    }


    public void deleteProduct(Long id) {
        webClient.delete()
                .uri("/products/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Transactional
    public void updatePrice(Product product, BigDecimal newPrice) {
        catalogService.updatePrice(product, newPrice);
    }
}

