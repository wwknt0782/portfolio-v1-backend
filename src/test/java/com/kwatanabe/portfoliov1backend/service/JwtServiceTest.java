package com.kwatanabe.portfoliov1backend.service;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private static final String SECRET = "test-jwt-secret-that-is-at-least-32-bytes-long";
    private static final Instant NOW = Instant.parse("2026-08-04T00:00:00Z");

    @Test
    void issuedTokenCanBeVerified() {
        JwtService service = serviceAt(NOW);
        String token = service.issueToken("admin");
        assertEquals("admin", service.verifyToken(token));
    }

    @Test
    void expiredTokenIsRejected() {
        String token = serviceAt(NOW).issueToken("admin");
        JwtService afterExpiration = serviceAt(NOW.plus(Duration.ofHours(9)));
        assertThrows(JwtException.class, () -> afterExpiration.verifyToken(token));
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() {
        String token = serviceAt(NOW).issueToken("admin");
        JwtService otherService = new JwtService(
                "another-test-secret-that-is-at-least-32-bytes",
                Duration.ofHours(8),
                Clock.fixed(NOW, ZoneOffset.UTC)
        );
        assertThrows(JwtException.class, () -> otherService.verifyToken(token));
    }

    private JwtService serviceAt(Instant instant) {
        return new JwtService(SECRET, Duration.ofHours(8), Clock.fixed(instant, ZoneOffset.UTC));
    }
}
