package com.kwatanabe.portfoliov1backend.config;

import com.resend.Resend;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(name = "app.mail.provider", havingValue = "resend")
public class ResendConfig {

    @Value("${app.mail.api-key:}")
    private String apikey;

    @PostConstruct
    void validateMailSettings() {
        if (!StringUtils.hasText(apikey)) {
            throw new IllegalStateException("app.mail.api-key is required when app.mail.provider=resend");
        }
    }

    @Bean
    Resend resend() {
        return Resend.builder()
                .apiKey(apikey)
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .build();
    }
}
