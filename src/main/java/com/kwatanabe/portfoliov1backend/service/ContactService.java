package com.kwatanabe.portfoliov1backend.service;

import com.kwatanabe.portfoliov1backend.dto.ContactRequestDto;
import com.kwatanabe.portfoliov1backend.entity.Contact;
import com.kwatanabe.portfoliov1backend.entity.MailStatus;
import com.kwatanabe.portfoliov1backend.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;
    private final MailService mailService;

    public void create(ContactRequestDto contactRequestDto) {
        Contact contact = createContact(contactRequestDto);
        Contact savedContact = contactRepository.save(contact);

        updateMailStatus(savedContact);

        contactRepository.save(savedContact);
    }

    private Contact createContact(ContactRequestDto contactRequestDto) {
        Contact contact = new Contact();

        contact.setName(contactRequestDto.getName());
        contact.setCompanyName(contactRequestDto.getCompanyName());
        contact.setEmail(contactRequestDto.getEmail());
        contact.setMessage(contactRequestDto.getMessage());
        contact.setCreatedAt(LocalDateTime.now());

        return contact;
    }

    private void updateMailStatus(Contact contact) {
        try {
            mailService.sendContactNotification(contact);
            contact.setMailStatus(MailStatus.SENT);
            contact.setMailSentAt(LocalDateTime.now());
            contact.setMailErrorMessage(null);
        } catch (Exception e) {
            contact.setMailStatus(MailStatus.FAILED);
            contact.setMailErrorMessage(getErrorMessage(e));
        }
    }

    private String getErrorMessage(Exception e) {
        if (e.getMessage() == null || e.getMessage().isBlank()) {
            return e.getClass().getSimpleName();
        }

        return e.getMessage();
    }
}
