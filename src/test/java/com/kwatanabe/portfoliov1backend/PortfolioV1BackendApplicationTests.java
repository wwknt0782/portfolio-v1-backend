package com.kwatanabe.portfoliov1backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PortfolioV1BackendApplicationTests {

    @Test
    void mainMethodCanBeReferenced() {
        assertDoesNotThrow(() -> PortfolioV1BackendApplication.class.getDeclaredMethod("main", String[].class));
    }

}
