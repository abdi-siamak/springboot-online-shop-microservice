package com.siamak.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendResetPasswordEmail (String to, String resetLink){
        Mono.fromRunnable(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Password Reset Request");
            message.setText("Click the following link to reset your password: " + resetLink);
            mailSender.send(message);
        }).subscribe();
    }
}
