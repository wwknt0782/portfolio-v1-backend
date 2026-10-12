package com.kwatanabe.portfoliov1backend.dto;

import com.kwatanabe.portfoliov1backend.enums.AuthStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthStatusResponse {

    private boolean authenticated;

    @NotBlank
    private AuthStatus status;
}
