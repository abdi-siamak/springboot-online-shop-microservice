package com.siamak.shop.dto;

import com.siamak.shop.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {
    private User user;
    private String recaptchaToken;
}
