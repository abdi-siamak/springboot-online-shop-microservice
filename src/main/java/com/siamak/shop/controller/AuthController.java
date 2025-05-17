package com.siamak.shop.controller;

import com.siamak.shop.dto.AuthRequest;
import com.siamak.shop.dto.LoginRequest;
import com.siamak.shop.dto.ResetPasswordRequest;
import com.siamak.shop.security.JwtUtils;
import com.siamak.shop.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AuthService authService;

    /*
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            String token = jwtUtils.generateToken(authRequest.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
     */

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        Map<String, String> responseBody = new HashMap<>();

        boolean captchaVerified = verifyCaptcha(request.getRecaptchaToken());
        if (!captchaVerified) {
            responseBody.put("message", "Captcha verification failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }

        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Generate the JWT token
        String token = jwtUtils.generateToken(userDetails.getUsername());

        // Set the JWT in both the Authorization header and cookies

        // Set in response header
        response.setHeader("Authorization", "Bearer " + token);

        // Set in cookie
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/"); // accessible from all paths
        cookie.setMaxAge(24 * 60 * 60); // 1 day
        response.addCookie(cookie);

        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Clear the JWT from cookies
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // expires immediately
        response.addCookie(cookie);

        response.setHeader("Authorization", "");

        return ResponseEntity.ok("Logged out");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authService.initiatePasswordReset(email);
        return ResponseEntity.ok("Password reset link sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request){
        boolean result = authService.resetPassword(request.getToken(), request.getNewPassword());
        return result
                ? ResponseEntity.ok("Password reset successful")
                : ResponseEntity.badRequest().body("Invalid or expired token");
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
