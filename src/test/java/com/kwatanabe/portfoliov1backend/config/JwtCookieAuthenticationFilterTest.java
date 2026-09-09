package com.kwatanabe.portfoliov1backend.config;

import com.kwatanabe.portfoliov1backend.service.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtCookieAuthenticationFilterTest {

    private final JwtService jwtService = mock(JwtService.class);
    private final JwtCookieAuthenticationFilter filter = new JwtCookieAuthenticationFilter(
            jwtService,
            "PORTFOLIO_AUTH",
            "https://portfolio.example.com"
    );

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void verifiesCookieOnProtectedApi() throws Exception {
        when(jwtService.verifyToken("valid-token")).thenReturn("admin");
        MockHttpServletRequest request = request("GET", "/auth/me");
        request.setCookies(new Cookie("PORTFOLIO_AUTH", "valid-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        verify(jwtService).verifyToken("valid-token");
        assertEquals("admin", SecurityContextHolder.getContext().getAuthentication().getName());
        assertEquals(200, response.getStatus());
    }

    @Test
    void skipsPublicContactApi() throws Exception {
        MockHttpServletRequest request = request("POST", "/api/contacts");
        request.setCookies(new Cookie("PORTFOLIO_AUTH", "ignored-token"));

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        verify(jwtService, never()).verifyToken("ignored-token");
    }

    @Test
    void rejectsProtectedMutationFromUnknownOrigin() throws Exception {
        when(jwtService.verifyToken("valid-token")).thenReturn("admin");
        MockHttpServletRequest request = request("PATCH", "/api/admin/resource");
        request.setCookies(new Cookie("PORTFOLIO_AUTH", "valid-token"));
        request.addHeader("Origin", "https://attacker.example");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals(403, response.getStatus());
    }

    private MockHttpServletRequest request(String method, String path) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        request.setServletPath(path);
        return request;
    }
}
