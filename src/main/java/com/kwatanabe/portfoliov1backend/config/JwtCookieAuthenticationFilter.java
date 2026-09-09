package com.kwatanabe.portfoliov1backend.config;

import com.kwatanabe.portfoliov1backend.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final String cookieName;
    private final Set<String> allowedOrigins;

    public JwtCookieAuthenticationFilter(
            JwtService jwtService,
            @Value("${app.auth.cookie-name:PORTFOLIO_AUTH}") String cookieName,
            @Value("${app.cors.allowed-origins}") String allowedOrigins
    ) {
        this.jwtService = jwtService;
        this.cookieName = cookieName;
        this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return HttpMethod.OPTIONS.matches(request.getMethod())
                || "/auth/login".equals(path)
                || "/swagger-ui.html".equals(path)
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/api-docs");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = findCookie(request);
        if (token != null) {
            try {
                // リクエストごとに署名と有効期限を検証する
                String username = jwtService.verifyToken(token);
                // 認証済みとして登録する
                var authentication = UsernamePasswordAuthenticationToken.authenticated(
                        username,
                        null,
                        List.of()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // SameSite=None の cookie を使うため、更新系 API は許可済み Origin に限定して CSRF を防ぐ。
                if (isUnsafeMethod(request) && !allowedOrigins.contains(request.getHeader("Origin"))) {
                    SecurityContextHolder.clearContext();
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            } catch (JwtException | IllegalArgumentException exception) {
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isUnsafeMethod(HttpServletRequest request) {
        return !HttpMethod.GET.matches(request.getMethod())
                && !HttpMethod.HEAD.matches(request.getMethod())
                && !HttpMethod.OPTIONS.matches(request.getMethod())
                && !HttpMethod.TRACE.matches(request.getMethod());
    }

    // リクエストのCookieからJWTを探す
    private String findCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
