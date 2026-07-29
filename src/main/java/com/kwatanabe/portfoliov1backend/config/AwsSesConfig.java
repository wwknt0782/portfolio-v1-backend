package com.kwatanabe.portfoliov1backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
@ConditionalOnProperty(name = "app.mail.provider", havingValue = "ses")
public class AwsSesConfig {

    @Bean
    public SesClient sesClient(
            @Value("${spring.cloud.aws.region.static:ap-northeast-1}") String region,
            @Value("${spring.cloud.aws.credentials.access-key:}") String accessKey,
            @Value("${spring.cloud.aws.credentials.secret-key:}") String secretKey
    ) {
        return SesClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider(accessKey, secretKey))
                .build();
    }

    private AwsCredentialsProvider credentialsProvider(String accessKey, String secretKey) {
        // アクセスキーが設定されている場合は認証に使う
        if (StringUtils.hasText(accessKey) && StringUtils.hasText(secretKey)) {
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
            );
        }

        // アクセスキーが設定されていない場合はAWS SDKデフォルトの認証を使う
        return DefaultCredentialsProvider.create();
    }
}
