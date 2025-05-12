package com.siamak.shop.service;

import com.siamak.shop.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    void updateUser(User user);
    void deleteUser(User user);
    boolean existsByUsername(String username);
}
