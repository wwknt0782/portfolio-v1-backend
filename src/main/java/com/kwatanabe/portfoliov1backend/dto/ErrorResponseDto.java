package com.kwatanabe.portfoliov1backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {
    private String message;
    private Map<String, String> errors;
}
