package com.kwatanabe.portfoliov1backend.controller;

import com.kwatanabe.portfoliov1backend.dto.ContactRequestDto;
import com.kwatanabe.portfoliov1backend.dto.ContactResponseDto;
import com.kwatanabe.portfoliov1backend.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor

public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<ContactResponseDto> create(
            @Valid @RequestBody ContactRequestDto requestDto
    ) {
        contactService.create(requestDto);

        return ResponseEntity.ok(
                new ContactResponseDto("問い合わせを受け付けました")
        );
    }

}
