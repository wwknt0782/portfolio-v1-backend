package com.kwatanabe.portfoliov1backend.service;

import com.kwatanabe.portfoliov1backend.entity.Contact;

public interface MailService {
    void sendContactNotification(Contact contact);
}
