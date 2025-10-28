package com.siamak.shop.client.user;

import com.siamak.shop.api.UserMapper;
import com.siamak.shop.dto.AuthRequest;
import com.siamak.shop.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class UserClient {
    private final WebClient webClient;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserClient.class);

    public UserClient(@Value("${user.service.url}") String userServiceUrl, UserService userService) {
        this.userService = userService;
        this.webClient = WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

    public List<User> getAllUsers() {
        return webClient.get()
                .uri("/users")
                .retrieve()
                .bodyToFlux(User.class)
                .collectList()
                .block();
    }

    public Optional<User> findById(Long id) {
        return webClient.get()
                .uri("/users/{id}", id)
                .retrieve()
                .bodyToMono(User.class)
                .onErrorResume(e -> Mono.empty())
                .blockOptional();
    }

    public Optional<User> findByEmail(String email) { // username = email
        return webClient.get()
                .uri("/users/email/{username}", email)
                .retrieve()
                .bodyToMono(User.class)
                .onErrorResume(e -> Mono.empty())
                .blockOptional();
    }

    public User registerUser(User user) {
        // Convert entity -> DTO before sending
        UserDto dtoToSend = UserMapper.toDto(user);
        log.info("Sending user: {}", user);
        // Send DTO and receive DTO response
        UserDto responseDto = webClient.post()
                .uri("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dtoToSend)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();

        // Convert received DTO -> entity
        return UserMapper.toEntity(responseDto);
    }

    public void deleteUser(Long id) {
        webClient.delete()
                .uri("/users/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public boolean validateCredentials(String username, String password) {
        AuthRequest validationRequest = new AuthRequest();
        validationRequest.setUsername(username);
        validationRequest.setPassword(password);
        try {
            Boolean isValid = webClient.post()
                    .uri("/users/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(validationRequest)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block(); // Blocking call to make the method synchronous

            return isValid != null && isValid;

        } catch (Exception e) {
            log.warn("Credential validation failed for user {}: {}", username, e.getMessage());
            return false;
        }
    }

    @Transactional
    public void updatePassword(User user, String newPassword) {
        userService.updatePassword(user, newPassword);
    }
}
