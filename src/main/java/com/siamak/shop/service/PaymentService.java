package com.siamak.shop.service;

import com.paypal.api.payments.Payment;

public interface PaymentService {

    Payment createPayment(Double total, String currency, String method, String intent, String description, String cancelUrl, String successUrl) throws PaymentException;
    Payment executePayment(String paymentId, String payerId) throws PaymentException;
}
