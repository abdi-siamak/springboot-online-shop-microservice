package com.siamak.shop.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ShippingData {
    private String fullName;
    private String phone;
    private String postalCode;
    private String country;
    private String city;
    private String street;
}