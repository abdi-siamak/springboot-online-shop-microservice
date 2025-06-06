package com.siamak.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ShippingDataRequest {
    private String fullName;
    private String phone;
    private String postalCode;
    private String country;
    private String city;
    private String street;
    private double amount;
}
