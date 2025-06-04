package com.siamak.shop.config;

import com.siamak.shop.filter.CsrfTokenResponseHeaderBindingFilter;
import com.siamak.shop.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    //private final CustomUserDetailsService userDetailsService;

    /*
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/**", "/api/products/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()); // for basic auth testing

        return http.build();
    }
     */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // Allow access to static resources like HTML, CSS, JS
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**").permitAll()
                        // Public endpoints
                        .requestMatchers("/api/auth/login", "/api/auth/logout","/api/users/register", "/actuator/prometheus").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        // Protected endpoints
                        .requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                        // Default: block anything not explicitly mentioned
                        .anyRequest().authenticated()
                )
                .with(new OAuth2LoginConfigurer<HttpSecurity>(), oauth2 ->
                        oauth2
                                .defaultSuccessUrl("/products", true)
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<CsrfTokenResponseHeaderBindingFilter> csrfHeaderFilter() {
        FilterRegistrationBean<CsrfTokenResponseHeaderBindingFilter> registrationBean =
                new FilterRegistrationBean<>(new CsrfTokenResponseHeaderBindingFilter());
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE); // Make sure it runs early
        return registrationBean;
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}