package com.kwatanabe.portfoliov1backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final String issuer;
    private final SecretKey signingKey;
    private final Duration tokenLifetime;
    private final Clock clock;

    @Autowired
    public JwtService(
            @Value("${app.jwt-secret}") String jwtSecret,
            @Value("${app.jwt-issuer}") String issuer,
            @Value("${app.auth.token-lifetime}") Duration tokenLifetime
    ) {
        // テスト時にここのclockの値を固定値に変える
        this(jwtSecret, issuer, tokenLifetime, Clock.systemUTC());
    }

    JwtService(String jwtSecret,String issuer, Duration tokenLifetime, Clock clock) {
        if (jwtSecret == null || jwtSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("app.jwt-secret must be at least 32 bytes");
        }
        this.issuer = issuer;
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.tokenLifetime = tokenLifetime;
        this.clock = clock;
    }

    // 署名付きJWTトークンを生成する
    public String issueToken(String username) {
        Instant now = clock.instant();
        return Jwts.builder()
                .issuer(issuer)
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(tokenLifetime)))
                .signWith(signingKey)
                .compact();
    }

    /**
     * 署名、有効期限、issuer を検証し、認証済みユーザー名を返す。
     * 不正な token の場合は JwtException を送出する。
     */
    public String verifyToken(String token) throws JwtException {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(issuer)
                .clock(() -> Date.from(clock.instant()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String subject = claims.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new JwtException("JWT subject is missing");
        }
        return subject;
    }
}
