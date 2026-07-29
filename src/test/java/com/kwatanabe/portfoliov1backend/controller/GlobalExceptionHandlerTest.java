package com.kwatanabe.portfoliov1backend.controller;

import com.kwatanabe.portfoliov1backend.dto.ContactRequestDto;
import com.kwatanabe.portfoliov1backend.dto.ErrorResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleValidationExceptionReturnsFieldErrors() throws NoSuchMethodException {
        ContactRequestDto requestDto = new ContactRequestDto();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "contactRequestDto");
        bindingResult.addError(new FieldError("contactRequestDto", "email", "メールアドレスの形式が正しくありません"));
        Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyEndpoint", ContactRequestDto.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("入力内容を確認してください", response.getBody().getMessage());
        assertEquals("メールアドレスの形式が正しくありません", response.getBody().getErrors().get("email"));
    }

    @Test
    void handleHttpMessageNotReadableExceptionReturnsEmptyErrors() {
        ResponseEntity<ErrorResponseDto> response =
                globalExceptionHandler.handleHttpMessageNotReadableException();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("リクエストボディの形式が正しくありません", response.getBody().getMessage());
        assertTrue(response.getBody().getErrors().isEmpty());
    }

    @Test
    void handleExceptionReturnsGenericMessage() {
        ResponseEntity<ErrorResponseDto> response =
                globalExceptionHandler.handleException(new RuntimeException("internal detail"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("サーバーエラーが発生しました", response.getBody().getMessage());
        assertTrue(response.getBody().getErrors().isEmpty());
    }

    @SuppressWarnings("unused")
    private void dummyEndpoint(ContactRequestDto requestDto) {
    }
}
