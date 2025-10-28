package com.siamak.shop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        try {
            String jwt = null;
            String username = null;

            // 1️. Try header first
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
            }

            // 2️. If no token in header, check cookies
            if (jwt == null) {
                jwt = getJwtFromCookies(request.getCookies());
            }

            // 3️. Validate and authenticate
            if (jwt != null && jwtUtils.validateToken(jwt)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                username = jwtUtils.extractUsername(jwt);
                if (username != null && !username.isBlank()) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException ex) {
            // user deleted or not found
            SecurityContextHolder.clearContext();
            clearJwtCookie(response);
        } catch (RuntimeException ex) {
            // expired or malformed JWT
            SecurityContextHolder.clearContext();
            clearJwtCookie(response);
        } finally {
            chain.doFilter(request, response);
        }
    }

    // Extracts JWT from cookies
    private String getJwtFromCookies(Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if ("jwt".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    // Clears invalid JWT cookie
    private void clearJwtCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // delete immediately
        // Optional hardening:
        // cookie.setSecure(true);             // enable under HTTPS
        // cookie.setAttribute("SameSite","Lax");
        response.addCookie(cookie);
    }
}
