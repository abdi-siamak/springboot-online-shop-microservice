package com.siamak.shop.service;

import com.siamak.shop.client.user.UserClient;
import com.siamak.shop.model.PasswordResetToken;
import com.siamak.shop.model.User;
import com.siamak.shop.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${SPRING_BOOT_URL_PATH}")
    private String SPRING_BOOT_URL_PATH;
    private final UserClient userClient;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public void initiatePasswordReset(String email) {
        // Generate a dummy userId for token
        Optional<User> user = userClient.findByEmail(email);
        if (user.isEmpty()) {
            return;
        }
        // Delete existing token if any
        tokenRepository.findByUserId(user.get().getId()).ifPresent(tokenRepository::delete);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .userId(user.get().getId())
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();

        tokenRepository.save(resetToken);

        String resetLink = SPRING_BOOT_URL_PATH + "/reset/reset-password?token=" + token;
        emailService.sendResetPasswordEmail(email, resetLink);
    }

    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) return false;
        Long userId = resetToken.getUserId();

        Optional<User> userOpt = userClient.findById(userId);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        userClient.updatePassword(user, passwordEncoder.encode(newPassword));
        tokenRepository.delete(resetToken);
        return true;
    }
}
