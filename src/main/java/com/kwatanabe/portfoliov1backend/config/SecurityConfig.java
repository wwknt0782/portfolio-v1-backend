package com.kwatanabe.portfoliov1backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ログイン用Basic認証
    @Bean
    @Order(1)
    public SecurityFilterChain loginSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/auth/login")
                // CorsConfigのCORS設定を利用する
                .cors(Customizer.withDefaults())
                // CSRFを無効にする
                .csrf(csrf -> csrf.disable())
                // サーバーセッションを利用しない
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 全てのリクエストに対して認証を必須にする
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                // Basic認証を有効にする
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // ログイン以外のAPIに適用する認証設定
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http,
            JwtCookieAuthenticationFilter jwtCookieAuthenticationFilter
    ) throws Exception {
        http
                // CorsConfigのCORS設定を利用する
                .cors(Customizer.withDefaults())
                // CSRFを無効にする
                .csrf(csrf -> csrf.disable())
                // サーバーセッションを利用しない
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 認証されていないユーザーからのリクエストは401を返す
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                // OPTIONSリクエストはすべて通し，それ以外は認証必須にする
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // ローカル開発でSwagger UIとOpenAPI定義を参照できるようにする
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated())
                // Basic認証より前にJWTフィルターによる認証チェックを実施する
                .addFilterBefore(jwtCookieAuthenticationFilter, BasicAuthenticationFilter.class);

        return http.build();
    }
}
