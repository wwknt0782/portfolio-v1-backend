package com.kwatanabe.portfoliov1backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.frontend-url}") String frontendUrl
    ) {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> allowedOrigins = List.of(frontendUrl, "http://localhost:3000");

        // 許可するフロントエンドURLを設定
        configuration.setAllowedOrigins(allowedOrigins);
        // 許可するHTTPメソッドを設定
        configuration.setAllowedMethods(List.of(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()
        ));
        // 許可するHTTPヘッダーを設定
        configuration.setAllowedHeaders(List.of(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT
        ));
        // フロントエンド側にLocationの読み取りを許可する
        configuration.setExposedHeaders(List.of(HttpHeaders.LOCATION));
        // Cookieの送受信を許可する
        configuration.setAllowCredentials(true);
        // プリフライト結果を1時間ブラウザにキャッシュさせる
        configuration.setMaxAge(3600L);

        // 全てのURLでCORS設定を適用する
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
