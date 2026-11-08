package com.kwatanabe.portfoliov1backend.service;

import com.kwatanabe.portfoliov1backend.dto.ContactRequest;
import com.kwatanabe.portfoliov1backend.entity.Contact;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.mail.provider", havingValue = "resend")
public class ResendMailService implements MailService{
    private final Resend resend;

    @Value("${app.mail.from-address:}")
            private String fromAddress;

    @Value("${app.mail.admin-address:}")
            private String adminAddress;

    // アプリ起動時にメールアドレスが設定されているかチェックする
    @PostConstruct
    void validateMailSettings() {
        if (!StringUtils.hasText(fromAddress)) {
            throw new IllegalStateException("app.mail.from-address is required when app.mail.provider=resend");
        }
        if (!StringUtils.hasText(adminAddress)) {
            throw new IllegalStateException("app.mail.admin-address is required when app.mail.provider=resend");
        }
    }

    @Override
    public void sendContactNotification(Contact contact) {
        CreateEmailOptions options = CreateEmailOptions.builder()
                .from(fromAddress)
                .to(adminAddress)
                .replyTo(contact.getEmail())
                .subject("ポートフォリオから問い合わせがありました")
                .text(createBody(contact))
                .build();

        try{
            resend.emails().send(options);
        } catch (ResendException e) {
            throw new MailSendException("メールの送信に失敗しました", e);
        }

    };

    private String createBody(Contact contact) {
        return """
                ポートフォリオから問い合わせがありました
                
                【お名前】
                %s
                
                【メールアドレス】
                %s
                
                【会社名】
                %s
                
                【お問い合わせ内容】
                %s
                """.formatted(
                        contact.getName(),
                contact.getEmail(),
                contact.getCompanyName(),
                contact.getMessage()

        );

    }
}
