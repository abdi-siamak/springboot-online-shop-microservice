package com.siamak.shop.controller;

import com.siamak.shop.client.catalog.CatalogClient;
import com.siamak.shop.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final CatalogClient catalogClient;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        return ResponseEntity.ok(catalogClient.addProduct(product));
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(catalogClient.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(catalogClient.getProductById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        catalogClient.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/price")
    public ResponseEntity<?> updatePrice (
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> body) {

        BigDecimal newPrice = body.get("price");
        Product product = catalogClient.getProductById(id);
        catalogClient.updatePrice(product, newPrice);
        return ResponseEntity.ok().build();
    }
}
