package com.kwatanabe.portfoliov1backend.controller;

import com.kwatanabe.portfoliov1backend.dto.AuthStatusResponse;
import com.kwatanabe.portfoliov1backend.dto.LoginRequest;
import com.kwatanabe.portfoliov1backend.dto.LoginResponse;
import com.kwatanabe.portfoliov1backend.dto.UserResponse;
import com.kwatanabe.portfoliov1backend.entity.User;
import com.kwatanabe.portfoliov1backend.repository.UserRepository;
import com.kwatanabe.portfoliov1backend.service.AuthService;
import com.kwatanabe.portfoliov1backend.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController{
    private final AuthService authService;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final String cookieName;
    private final Duration tokenLifetime;
    private final Boolean cookieSecure;
    private final String cookieSameSite;

    public AuthController(
            AuthService authService,
            UserRepository userRepository,
            @Value("${app.auth.cookie-name}")String cookieName,
            @Value("${app.auth.token-lifetime}")Duration tokenLifetime,
            @Value("${app.auth.cookie-secure}")Boolean cookieSecure,
            @Value("${app.auth.cookie-same-site}")String cookieSameSite){
        this.authService = authService;
        this.userRepository = userRepository;
        this.cookieName = cookieName;
        this.tokenLifetime = tokenLifetime;
        this.cookieSecure = cookieSecure;
        this.cookieSameSite = cookieSameSite;
    }

    // ユーザー名とパスワードを受け取る
    // サービス層で認証→OKならJWT組み立て
    // JWT付きレスポンスを返す
    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        // リクエストのユーザー名とパスワードをDBで照合する
        // AuthService.login(); ユーザー名とパスワードを受け取って認証したらJWTを返す

        String jwt;
        try{
            jwt = authService.login(request.getUserName(), request.getPassword());
        } catch (Error error) {
            return new ResponseEntity<>(new LoginResponse("ユーザー名またはパスワードが正しくありません"), HttpStatus.UNAUTHORIZED);
        }
        // JWTをCookieに設定する
        ResponseCookie cookie = ResponseCookie.from(cookieName, jwt)
                .httpOnly(true)
                .secure(cookieSecure) // todo 本番環境ではtrue
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(tokenLifetime)
                .build();

        // JWT付きレスポンスを返す
        LoginResponse response = new LoginResponse("ログインに成功しました");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @GetMapping("/admin/status")
    public ResponseEntity<AuthStatusResponse> getAuthStatus(
            Authentication authentication
    ) {
return ResponseEntity.ok(
        authService.getAuthStatus(authentication)
);
    }

    @GetMapping("/admin/me")
    public ResponseEntity<UserResponse> me(Authentication authentication){
        String username = authentication.getName();
        Optional<User> optionalUser = userRepository.findByName(username);

        if(optionalUser.isEmpty()) {
            return new ResponseEntity<>(new UserResponse("9999","username" ,new String[]{""}), HttpStatus.UNAUTHORIZED);
        }

        User user = optionalUser.get();
        String id = user.getId().toString();
        String userName = user.getName();

        UserResponse response = new UserResponse(id, userName,new String[]{""});
        return ResponseEntity.ok(response);
    }

@PostMapping("/logout")
    public ResponseEntity<Void> logout(){
ResponseCookie cookie = ResponseCookie.from(cookieName, "")
        .httpOnly(true)
        .secure(false) // todo 本番環境ではtrue
        .sameSite("Strict")
        .path("/")
        .maxAge(Duration.ZERO)
        .build();

return ResponseEntity.noContent()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .build();
    }

}
