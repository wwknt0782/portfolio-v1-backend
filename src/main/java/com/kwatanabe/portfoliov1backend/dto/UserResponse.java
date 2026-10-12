package com.kwatanabe.portfoliov1backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String userName;
    private String[] roles;
}
