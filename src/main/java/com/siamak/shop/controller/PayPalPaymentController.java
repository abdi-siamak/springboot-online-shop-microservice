package com.siamak.shop.controller;

import com.siamak.shop.service.PaymentException;
import com.siamak.shop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paypal")
@RequiredArgsConstructor
public class PayPalPaymentController {

    private final PaymentService payPalService;
    @Value("${SPRING_BOOT_URL_PATH}")
    private String SPRING_BOOT_URL_PATH;

    @PostMapping("/create-payment")
    public ResponseEntity<String> createPayment(@RequestParam("amount") Double amount) {
        try {
            Payment payment = payPalService.createPayment(
                    amount,
                    "EUR",
                    "paypal",
                    "sale",
                    "Payment for order",
                    SPRING_BOOT_URL_PATH+"/paypal/cancel",
                    SPRING_BOOT_URL_PATH+"/paypal/success"
            );

            for (Links link : payment.getLinks()) {
                if (link.getRel().equalsIgnoreCase("approval_url")) {
                    // TO Do: update the status of the order

                    return ResponseEntity.ok(link.getHref()); // Return redirect URL to frontend
                }
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Approval URL not found");

        } catch (PaymentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred during payment creation");
        }
    }

    @GetMapping("/success")
    public ResponseEntity<String> successPayment(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId) {

        try {
            Payment payment = payPalService.executePayment(paymentId, payerId);

            if ("approved".equalsIgnoreCase(payment.getState())) {
                // Here we can update order status in your database
                return ResponseEntity.ok("Payment successful");
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment not approved");

        } catch (PaymentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred during payment execution");
        }
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> cancelPayment() {
        return ResponseEntity.ok("Payment cancelled by user");
    }
}
