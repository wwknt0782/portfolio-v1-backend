package com.kwatanabe.portfoliov1backend.service;

import com.kwatanabe.portfoliov1backend.dto.ContactRequestDto;
import com.kwatanabe.portfoliov1backend.entity.Contact;
import com.kwatanabe.portfoliov1backend.entity.MailStatus;
import com.kwatanabe.portfoliov1backend.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContactServiceTest {
    private ContactRepository contactRepository;
    private MailService mailService;
    private ContactService contactService;

    @BeforeEach
    void setUp() {
        contactRepository = mock(ContactRepository.class);
        mailService = mock(MailService.class);
        contactService = new ContactService(contactRepository, mailService);

        when(contactRepository.save(any(Contact.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createSetsMailStatusSentWhenNotificationSucceeds() {
        ContactRequestDto requestDto = new ContactRequestDto(
                "山田太郎",
                "Example Inc.",
                "taro@example.com",
                "問い合わせ内容です。"
        );

        contactService.create(requestDto);

        Contact savedContact = captureLastSavedContact();

        verify(mailService).sendContactNotification(any(Contact.class));
        assertEquals("山田太郎", savedContact.getName());
        assertEquals("Example Inc.", savedContact.getCompanyName());
        assertEquals("taro@example.com", savedContact.getEmail());
        assertEquals("問い合わせ内容です。", savedContact.getMessage());
        assertEquals(MailStatus.SENT, savedContact.getMailStatus());
        assertNotNull(savedContact.getMailSentAt());
        assertNull(savedContact.getMailErrorMessage());
    }

    @Test
    void createSetsMailStatusFailedWhenNotificationFails() {
        doThrow(new RuntimeException("SES send failed"))
                .when(mailService)
                .sendContactNotification(any(Contact.class));

        ContactRequestDto requestDto = new ContactRequestDto(
                "山田太郎",
                "",
                "taro@example.com",
                "問い合わせ内容です。"
        );

        contactService.create(requestDto);

        Contact savedContact = captureLastSavedContact();

        assertEquals(MailStatus.FAILED, savedContact.getMailStatus());
        assertEquals("SES send failed", savedContact.getMailErrorMessage());
        assertNull(savedContact.getMailSentAt());
    }

    private Contact captureLastSavedContact() {
        ArgumentCaptor<Contact> contactCaptor = ArgumentCaptor.forClass(Contact.class);
        verify(contactRepository, times(2)).save(contactCaptor.capture());

        List<Contact> savedContacts = contactCaptor.getAllValues();

        return savedContacts.get(savedContacts.size() - 1);
    }
}
