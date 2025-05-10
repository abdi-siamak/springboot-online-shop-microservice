package com.siamak.shop.service;

import com.siamak.shop.model.User;

import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
