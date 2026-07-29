package com.kwatanabe.portfoliov1backend.service;

import com.kwatanabe.portfoliov1backend.entity.Contact;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.mail.provider", havingValue = "ses")
public class SesMailService implements MailService {
    private static final String CHARSET = "UTF-8";

    private final SesClient sesClient;

    @Value("${app.mail.from-address:}")
    private String fromAddress;

    @Value("${app.mail.admin-address:}")
    private String adminAddress;

    // アプリ起動時にメールアドレスが設定されているかチェックする
    @PostConstruct
    void validateMailSettings() {
        if (!StringUtils.hasText(fromAddress)) {
            throw new IllegalStateException("app.mail.from-address is required when app.mail.provider=ses");
        }
        if (!StringUtils.hasText(adminAddress)) {
            throw new IllegalStateException("app.mail.admin-address is required when app.mail.provider=ses");
        }
    }

    @Override
    public void sendContactNotification(Contact contact) {
        SendEmailRequest request = SendEmailRequest.builder()
                .source(fromAddress)
                .destination(Destination.builder()
                        .toAddresses(adminAddress)
                        .build())
                .replyToAddresses(contact.getEmail())
                .message(Message.builder()
                        .subject(content("ポートフォリオサイトから問い合わせがありました"))
                        .body(Body.builder()
                                .text(content(createBody(contact)))
                                .build())
                        .build())
                .build();

        sesClient.sendEmail(request);
    }

    private Content content(String text) {
        return Content.builder()
                .charset(CHARSET)
                .data(text)
                .build();
    }

    private String createBody(Contact contact) {
        return """
                ポートフォリオサイトから問い合わせがありました。

                ID:
                %d

                お名前:
                %s

                会社名:
                %s

                メールアドレス:
                %s

                お問い合わせ内容:
                %s
                """.formatted(
                contact.getId(),
                contact.getName(),
                blankToHyphen(contact.getCompanyName()),
                contact.getEmail(),
                contact.getMessage()
        );
    }

    private String blankToHyphen(String value) {
        if (!StringUtils.hasText(value)) {
            return "-";
        }

        return value;
    }
}
