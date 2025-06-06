package com.siamak.shop.controller;

import com.siamak.shop.dto.ShippingDataRequest;
import com.siamak.shop.model.*;
import com.siamak.shop.service.CartService;
import com.siamak.shop.service.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    @PostMapping("/save")
    public ResponseEntity<Void> save(@RequestBody ShippingDataRequest request, @AuthenticationPrincipal User currentUser) {
        // Create a new order
        Order newOrder = new Order();
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setStatus("PENDING");
        newOrder.setUser(currentUser);
        newOrder.setShippingData(new ShippingData(
                request.getFullName(),
                request.getPhone(),
                request.getPostalCode(),
                request.getCountry(),
                request.getCity(),
                request.getStreet()
        ));
        newOrder.setAmount(request.getAmount());

        // Convert CartItems to OrderItems
        List<CartItem> cartItems = cartService.getItems();

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            return OrderItem.builder()
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getTotalPrice())
                    .order(newOrder)
                    .build();
        }).collect(Collectors.toList());

        newOrder.setItems(orderItems);

        orderService.save(newOrder);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/retrieve")
    public ResponseEntity retrieve(@RequestBody User user) {
        orderService.retrieve(user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/update")
    public ResponseEntity update(@RequestBody Long orderId, String status) {
        orderService.updateOrderStatus(orderId, status);

        return ResponseEntity.ok().build();
    }

}
