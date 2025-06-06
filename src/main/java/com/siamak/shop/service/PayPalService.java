package com.siamak.shop.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PayPalService implements PaymentService{

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    private APIContext getApiContext() {
        return new APIContext(clientId, clientSecret, mode);
    }

    @Override
    public Payment createPayment(Double total, String currency, String method, String intent, String description, String cancelUrl, String successUrl) throws PaymentException {
        try {
            Amount amount = new Amount();
            amount.setCurrency(currency);
            amount.setTotal(String.format("%.2f", total));

            Transaction transaction = new Transaction();
            transaction.setDescription(description);
            transaction.setAmount(amount);

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod(method);

            Payment payment = new Payment();
            payment.setIntent(intent);
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl(cancelUrl);
            redirectUrls.setReturnUrl(successUrl);
            payment.setRedirectUrls(redirectUrls);

            return payment.create(getApiContext());
        } catch (PayPalRESTException e) {
            throw new PaymentException("Error creating PayPal payment", e);
        }
    }

    @Override
    public Payment executePayment(String paymentId, String payerId) throws PaymentException {
        try {
            Payment payment = new Payment();
            payment.setId(paymentId);

            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);

            return payment.execute(getApiContext(), paymentExecution);
        } catch (PayPalRESTException e) {
            throw new PaymentException("Error executing PayPal payment", e);
        }
    }
}