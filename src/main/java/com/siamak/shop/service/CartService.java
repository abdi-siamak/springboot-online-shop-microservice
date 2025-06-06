package com.siamak.shop.service;

import com.siamak.shop.model.CartItem;
import com.siamak.shop.model.Product;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CartService {

    private final Map<Long, CartItem> items = new HashMap<>();

    public void addProduct(Product product, int quantity) {
        if (items.containsKey(product.getId())) {
            CartItem item = items.get(product.getId());
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            items.put(product.getId(), new CartItem(product, quantity));
        }
    }

    public void removeProduct(Long productId) {
        items.remove(productId);
    }

    public void updateQuantity(Long productId, int quantity) {
        CartItem item = items.get(productId);
        if (item != null) {
            item.setQuantity(quantity);
        }
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }

    public void clearCart() {
        items.clear();
    }

    public int getTotalNumberOfItems() {
        return items.size();
    }
}