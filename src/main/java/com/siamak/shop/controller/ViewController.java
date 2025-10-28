package com.siamak.shop.controller;

import com.siamak.shop.client.catalog.CatalogClient;
import com.siamak.shop.client.user.UserClient;
import com.siamak.shop.model.CartItem;
import com.siamak.shop.model.Order;
import com.siamak.shop.model.User;
import com.siamak.shop.service.CartService;
import com.siamak.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class ViewController {

    private final CartService cartService;
    private final CatalogClient catalogClient;
    private final UserClient userClient;
    private final OrderService orderService;
    @Value("${paypal.client.id}")
    private String paypalClientId;

    @GetMapping("/")
    public String index() {
        return "redirect:/products";
    }

    @GetMapping("/loginPage")
    public String login() {
        return "loginPage";}

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String admin(Model model, Authentication authentication) {
        model.addAttribute("productItems", catalogClient.getAllProducts());
        model.addAttribute("users", userClient.getAllUsers());

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            Optional<User> currentUser = userClient.findByEmail(userDetails.getUsername());
            currentUser.ifPresent(user -> { model.addAttribute("loggedInUserId", user.getId());
            });
        }

        return "admin";
    }

    @GetMapping("/products")
    public String products(Model model, Authentication authentication) {
        try {
            model.addAttribute("productItems", catalogClient.getAllProducts());
        } catch (Exception ex) {
            model.addAttribute("productItems", List.of());
            model.addAttribute("loadError", ex.getMessage());
            // log ex
        }
        if (authentication != null) {
            model.addAttribute("authenticatedUser", authentication.getPrincipal());
        }
            /*
            if (principal instanceof UserDetails userDetails) {
                email = userDetails.getUsername();
            } else if (principal instanceof OAuth2User oauth2User) {
                email = oauth2User.getAttribute("email");
            }
        }
        if (email == null) {
            throw new RuntimeException("Unable to extract email from authentication");
        }

        Optional<User> user = userService.findByEmail(email);
        User cartUser = user.orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("userRole", cartUser.getRole());
        */
        return "products";
    }

    @GetMapping("/cart")
    public String cart(Model model, Principal principal) {
        List<CartItem> cartItems;
        String userEmail = null;

        if (principal != null) {
            userEmail = principal.getName();
            cartItems = cartService.getItems();
            Optional<User> user = userClient.findByEmail(userEmail);
            User cartUser = user.orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("authenticatedUser", principal);
            model.addAttribute("userRole", cartUser.getRole());
            model.addAttribute("user", cartUser.getName());
        } else {
            // Guest cart
            cartItems = cartService.getItems();
            model.addAttribute("userRole", "GUEST");
        }

        BigDecimal totalPrice = cartItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice.doubleValue());

        return "cart";
    }

    @GetMapping("/payment")
    public String paymentPage(Model model) {
        model.addAttribute("clientId", paypalClientId);
        model.addAttribute("totalPrice", cartService.getItems().stream().map(CartItem::getTotalPrice).reduce(BigDecimal.ZERO,  BigDecimal::add));

        return "payment";
    }

    @GetMapping("/orders")
    public String orders(Model model, Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userClient.findByEmail(email);

        if (userOpt.isEmpty()) {
            return "redirect:/error";
        }

        Long userId = userOpt.get().getId();
        List<Order> userOrders = orderService.retrieveOrdersByUserId(userId);
        model.addAttribute("userOrders", userOrders);

        return "orders";
    }
}