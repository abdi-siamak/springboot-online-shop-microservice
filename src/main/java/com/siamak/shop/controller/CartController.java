package com.siamak.shop.controller;

import com.siamak.shop.client.catalog.CatalogClient;
import com.siamak.shop.model.CartItem;
import com.siamak.shop.model.Product;
import com.siamak.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CatalogClient catalogClient;

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestParam Long productId, @RequestParam int quantity) {
        Product product = catalogClient.getProductById(productId);
        if (product == null) {
            return ResponseEntity.badRequest().body("Product not found");
        }
        cartService.addProduct(product, quantity);
        return ResponseEntity.ok("Product added to cart");
    }

    @GetMapping
    public List<CartItem> getCartItems() {
        return cartService.getItems();
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateQuantity(@RequestParam Long productId, @RequestParam int quantity) {
        cartService.updateQuantity(productId, quantity);
        return ResponseEntity.ok("Cart updated");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFromCart(@RequestParam Long productId) {
        cartService.removeProduct(productId);
        return ResponseEntity.ok("Product removed from cart");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart() {
        cartService.clearCart();
        return ResponseEntity.ok("Cart cleared");
    }
}
