package com.siamak.shop.controller;

import com.siamak.shop.dto.ShippingDataRequest;
import com.siamak.shop.model.*;
import com.siamak.shop.service.CartService;
import com.siamak.shop.service.OrderService;
import com.siamak.shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<Void> save(@RequestBody ShippingDataRequest request, Authentication authentication) {
        String email = authentication.getName(); // or username
        Optional<User> optionalUser = userService.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = optionalUser.get();
        // Create a new order
        Order newOrder = new Order();
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setStatus("PENDING");
        newOrder.setUser(currentUser);///****
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
                    .productId(cartItem.getProduct().getId())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getTotalPrice())
                    .order(newOrder)
                    .build();
        }).collect(Collectors.toList());

        newOrder.setItems(orderItems);///****

        orderService.save(newOrder);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("order_id") Long orderId) {
        System.out.println("order removed   "+orderId);
        orderService.deleteOrder(orderId);
        return "redirect:/orders"; // HTTP 302 redirect response to the client.
    }


    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody Long orderId, String status) {
        orderService.updateOrderStatus(orderId, status);

        return ResponseEntity.ok().build();
    }

}
