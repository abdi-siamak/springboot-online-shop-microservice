package com.siamak.shop.controller;

import com.siamak.shop.model.User;
import com.siamak.shop.service.CartService;
import com.siamak.shop.service.ProductService;
import com.siamak.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
public class ViewController {

    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String admin(Model model, Authentication authentication) {
        model.addAttribute("productItems", productService.getAllProducts());
        model.addAttribute("users", userService.findAll());

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            Optional<User> currentUser = userService.findByEmail(userDetails.getUsername());
            currentUser.ifPresent(user -> { model.addAttribute("loggedInUserId", user.getId());
            });
        }

        return "admin";
    }

    @GetMapping("/products")
    public String products(Model model, Authentication authentication) {

        model.addAttribute("productItems", productService.getAllProducts());

        String email = null;
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
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

        return "products";
    }

    @GetMapping("/cart")
    public String cart(Model model, Principal principal) {
        model.addAttribute("cartItems", cartService.getItems()); // Make cartItems available in the HTML template as a variable.
        Optional<User> user = userService.findByEmail(principal.getName());
        User cartUser = user.orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("userRole", cartUser.getRole());

        return "cart";
    }
}