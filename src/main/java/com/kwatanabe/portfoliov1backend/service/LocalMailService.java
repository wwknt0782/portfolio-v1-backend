package com.kwatanabe.portfoliov1backend.service;

import com.kwatanabe.portfoliov1backend.entity.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.mail.provider", havingValue = "local", matchIfMissing = true)
public class LocalMailService implements MailService {
    private static final Logger logger = LoggerFactory.getLogger(LocalMailService.class);

    @Override
    public void sendContactNotification(Contact contact) {
        logger.info(
                "Contact notification: id={}, name={}, companyName={}, email={}, message={}",
                contact.getId(),
                contact.getName(),
                contact.getCompanyName(),
                contact.getEmail(),
                contact.getMessage()
        );
    }
}
