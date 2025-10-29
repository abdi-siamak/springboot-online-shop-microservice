package com.siamak.shop.repository;

import com.siamak.shop.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
    Optional<PasswordResetToken> findByUserId(Long userId);
}
