package com.siamak.shop.controller;

import com.siamak.shop.model.User;
import com.siamak.shop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /*
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            System.out.println(userService.existsByUsername(user.getUsername()));
            if (userService.existsByUsername(user.getUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User has already registered!");
            } else {
                userService.registerUser(user);
                return ResponseEntity.ok("User registered successfully.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }
    */

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        try {
            if (userService.existsByUsername(user.getUsername())) {
                response.put("message", "User has already registered!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                userService.registerUser(user);
                response.put("message", "User registered successfully.");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
