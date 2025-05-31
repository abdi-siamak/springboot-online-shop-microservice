package com.siamak.shop.security;

import com.siamak.shop.model.Role;
import com.siamak.shop.model.User;
import com.siamak.shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Extract info from Google profile
        String name = oAuth2User.getAttribute("name");
        String email = oAuth2User.getAttribute("email");

        // Check if user exists
        User user = userService.findByEmail(email).orElseGet(() -> {
            // Register new user
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword("");
            newUser.setRole(Role.USER);
            return userService.registerUser(newUser);
        });

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                oAuth2User.getAttributes(),
                "email" // used as key for getName()
        );
    }
}
