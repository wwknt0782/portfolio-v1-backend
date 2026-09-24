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

    // 環境変数を受け取る
    @Autowired
    public JwtService(
            @Value("${app.jwt-secret}") String jwtSecret, // 秘密鍵
            @Value("${app.jwt-issuer}") String issuer,
            @Value("${app.auth.token-lifetime}") Duration tokenLifetime
    ) {
        // 同じクラスの別コンストラクタを呼ぶ
        // テスト時はここのclockの値を固定値に変える
        this(jwtSecret, issuer, tokenLifetime, Clock.systemUTC());
    }

    // その他のフィールドを決定するコンストラクタ
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
                .issuer(issuer) // トークン発行者
                .subject(username) // ユーザー名
                .issuedAt(Date.from(now)) // 発行時刻
                .expiration(Date.from(now.plus(tokenLifetime))) // 有効期限
                .signWith(signingKey) // 署名用秘密鍵
                .compact();
    }

     // 署名、有効期限、issuer を検証し、認証済みユーザー名を返す
     // 不正な token の場合は JwtException を送出する
    public String verifyToken(String token) throws JwtException {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey) // 秘密鍵で署名を検証する設定
                .requireIssuer(issuer) // issueを検証する設定
                .clock(() -> Date.from(clock.instant())) // 現在時刻が有効期限内か検証する設定
                .build()
                .parseSignedClaims(token) // 受け取ったJWTトークンを検証する
                .getPayload(); // JWTトークンのペイロード部分を取得する

        // JWTに設定されたユーザー名を返す
        String subject = claims.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new JwtException("JWT subject is missing");
        }
        return subject;
    }
}
