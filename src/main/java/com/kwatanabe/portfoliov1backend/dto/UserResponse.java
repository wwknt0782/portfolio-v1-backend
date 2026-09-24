package com.kwatanabe.portfoliov1backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    public String id;
    public String name;
    public String message;
}
