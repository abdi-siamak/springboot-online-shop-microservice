package com.siamak.shop.controller;

import com.siamak.shop.dto.RegisterRequest;
import com.siamak.shop.model.User;
import com.siamak.shop.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import com.siamak.shop.security.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

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
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest registerRequest, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> responseBody = new HashMap<>();
        User user = registerRequest.getUser();

        boolean captchaVerified = verifyCaptcha(registerRequest.getRecaptchaToken());

        if (!captchaVerified) {
            responseBody.put("message", "Captcha verification failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }

        try {
            if (userService.existsByEmail(user.getEmail())) {
                responseBody.put("message", "User has already registered!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
            }
            userService.registerUser(user);

            // Check for existing JWT cookie
            String existingToken = null;
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("jwt".equals(cookie.getName())) {
                        existingToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (existingToken == null || !jwtUtils.validateToken(existingToken)) {
                String token = jwtUtils.generateToken(user.getEmail());
                response.setHeader("Authorization", "Bearer " + token);
                Cookie cookie = new Cookie("jwt", token);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(24 * 60 * 60); // 1 day
                response.addCookie(cookie);
            }

            responseBody.put("message", "User registered successfully.");
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            responseBody.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id, Principal principal) {
        Optional<User> currentUser = userService.findByEmail(principal.getName());

        if (currentUser.isPresent() && currentUser.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot delete yourself.");
        }

        Optional<User> user = userService.findById(id);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        userService.deleteUser(user.get());
        return ResponseEntity.noContent().build();
    }

    public boolean verifyCaptcha(String token) {
        //System.out.println("CAPTCHA received token: " + token);
        /*
        String secretKey = "6LfCVD0rAAAAACsoVJR4_ft-OI6r9tjVfUVPTCHo";
        String url = "https://www.google.com/recaptcha/api/siteverify";

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("secret", secretKey);
        requestBody.add("response", token);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

        Map<String, Object> body = response.getBody();
        System.out.println("CAPTCHA verification response: " + body);

        return body != null && Boolean.TRUE.equals(body.get("success"));

         */
        return !token.isEmpty();
    }



}
