package com.siamak.shop.controller;

import com.siamak.shop.service.PaypalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/paypal")
public class PaypalController {

    @Autowired
    private PaypalService paypalService;

    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestParam("amount") double amount) {
        try {
            String approvalUrl = paypalService.createOrder(amount);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(approvalUrl)).build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Payment failed.");
        }
    }

    @GetMapping("/success")
    public String success(@RequestParam("token") String orderId) {
        try {
            if (paypalService.captureOrder(orderId)) {
                return "Payment successful!";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Payment failed.";
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "Payment canceled.";
    }
}
