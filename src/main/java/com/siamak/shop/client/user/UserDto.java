package com.siamak.shop.client.user;

import com.siamak.shop.model.Role;

public record UserDto (
        Long id,
        String name,
        String email,
        String password,
        Role role
) { }
