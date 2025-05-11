package com.siamak.shop.controller;

import com.siamak.shop.service.CartService;
import com.siamak.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("productItems", productService.getAllProducts());
        return "products";
    }

    @GetMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("cartItems", cartService.getItems()); // Make cartItems available in the HTML template as a variable.
        return "cart";
    }
}