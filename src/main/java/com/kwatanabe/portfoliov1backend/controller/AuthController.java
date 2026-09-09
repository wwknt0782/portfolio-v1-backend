package com.kwatanabe.portfoliov1backend.controller;

import com.kwatanabe.portfoliov1backend.dto.AuthMeResponseDto;
import com.kwatanabe.portfoliov1backend.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Duration;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final String frontendUrl;
    private final String cookieName;
    private final boolean cookieSecure;
    private final String cookieSameSite;
    private final Duration tokenLifetime;

    public AuthController(
            JwtService jwtService,
            @Value("${app.frontend-url}") String frontendUrl,
            @Value("${app.auth.cookie-name:PORTFOLIO_AUTH}") String cookieName,
            @Value("${app.auth.cookie-secure:false}") boolean cookieSecure,
            @Value("${app.auth.cookie-same-site:Lax}") String cookieSameSite,
            @Value("${app.auth.token-lifetime:PT8H}") Duration tokenLifetime
    ) {
        this.jwtService = jwtService;
        this.frontendUrl = frontendUrl;
        this.cookieName = cookieName;
        this.cookieSecure = cookieSecure;
        this.cookieSameSite = cookieSameSite;
        this.tokenLifetime = tokenLifetime;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login(Authentication authentication) {
        String token = jwtService.issueToken(authentication.getName());
        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(tokenLifetime)
                .build();

        return ResponseEntity.status(302)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .location(URI.create(frontendUrl))
                .build();
    }

    @GetMapping("/me")
    public AuthMeResponseDto me(Authentication authentication) {
        return new AuthMeResponseDto(true, authentication.getName());
    }
}
