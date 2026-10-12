package com.kwatanabe.portfoliov1backend.enums;

public enum AuthStatus {
        AUTHENTICATED,
        TOKEN_EXPIRED,
        MFA_TOKEN_EXPIRED,
        NOT_AUTHENTICATED,
        MFA_REQUIRED
}
