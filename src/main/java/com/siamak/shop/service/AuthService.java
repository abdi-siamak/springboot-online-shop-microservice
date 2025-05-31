package com.siamak.shop.service;

import com.siamak.shop.model.PasswordResetToken;
import com.siamak.shop.model.User;
import com.siamak.shop.repository.PasswordResetTokenRepository;
import com.siamak.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public void initiatePasswordReset(String email) {
        if(userRepository.findByEmail(email).isEmpty()){
            return;
        }

        User user = userRepository.findByEmail(email).get();

        // Check if a token already exists for the user
        Optional<PasswordResetToken> existingTokenOpt = tokenRepository.findByUser(user);
        existingTokenOpt.ifPresent(tokenRepository::delete);
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();

        tokenRepository.save(resetToken);
        String resetLink = "http://localhost:8080/reset/reset-password?token=" + token;

        emailService.sendResetPasswordEmail(user.getEmail(), resetLink);
    }

    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) return false;

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
        return true;
    }
}
