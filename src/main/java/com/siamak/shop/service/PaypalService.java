package com.siamak.shop.service;

import com.paypal.core.PayPalHttpClient;

import com.paypal.http.HttpResponse;
import com.paypal.orders.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PaypalService {

    @Autowired
    private PayPalHttpClient payPalClient;
    @Value("${SPRING_BOOT_URL_PATH}")
    private String SPRING_BOOT_URL_PATH;

    public String createOrder(double amount) throws IOException {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        ApplicationContext applicationContext = new ApplicationContext()
                .cancelUrl(SPRING_BOOT_URL_PATH+"/api/paypal/cancel")
                .returnUrl(SPRING_BOOT_URL_PATH+"/api/paypal/success");

        orderRequest.applicationContext(applicationContext);

        AmountWithBreakdown amountWithBreakdown = new AmountWithBreakdown()
                .currencyCode("EUR")
                .value(String.format("%.2f", amount));

        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
                .amountWithBreakdown(amountWithBreakdown);

        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));

        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
        HttpResponse<Order> response = payPalClient.execute(request);

        for (LinkDescription link : response.result().links()) {
            if ("approve".equals(link.rel())) {
                return link.href(); // redirect user to this PayPal URL
            }
        }
        return null;
    }

    public boolean captureOrder(String orderId) throws IOException {
        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
        HttpResponse<Order> response = payPalClient.execute(request);

        return "COMPLETED".equals(response.result().status());
    }
}
