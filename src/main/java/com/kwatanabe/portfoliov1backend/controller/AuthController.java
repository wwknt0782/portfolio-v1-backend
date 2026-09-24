package com.kwatanabe.portfoliov1backend.controller;

import com.kwatanabe.portfoliov1backend.dto.LoginRequest;
import com.kwatanabe.portfoliov1backend.dto.LoginResponse;
import com.kwatanabe.portfoliov1backend.dto.UserResponse;
import com.kwatanabe.portfoliov1backend.entity.User;
import com.kwatanabe.portfoliov1backend.repository.UserRepository;
import com.kwatanabe.portfoliov1backend.service.AuthService;
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

    public AuthController(
            AuthService authService,
            UserRepository userRepository,
            @Value("${app.auth.cookie-name}")String cookieName,
            @Value("${app.auth.token-lifetime}")Duration tokenLifetime){
        this.authService = authService;
        this.userRepository = userRepository;
        this.cookieName = cookieName;
        this.tokenLifetime = tokenLifetime;
    }

    // ユーザー名とパスワードを受け取る
    // サービス層で認証→OKならJWT組み立て
    // JWT付きレスポンスを返す
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        // リクエストのユーザー名とパスワードをDBで照合する
        // AuthService.login(); ユーザー名とパスワードを受け取って認証したらJWTを返す
        log.info(request.getUserName()+"/"+request.getPassword());

        String jwt;
        try{
            jwt = authService.login(request.getUserName(), request.getPassword());
        } catch (Error error) {
            return new ResponseEntity<>(new LoginResponse("ユーザー名またはパスワードが正しくありません"), HttpStatus.UNAUTHORIZED);
        }
        // JWTをCookieに設定する
        ResponseCookie cookie = ResponseCookie.from(cookieName, jwt)
                .httpOnly(true)
                .secure(false) // todo 本番環境ではtrue
                .sameSite("Strict")
                .path("/")
                .maxAge(tokenLifetime)
                .build();

        // JWT付きレスポンスを返す
        LoginResponse response = new LoginResponse("ログインに成功しました");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication){
        String username = authentication.getName();
        Optional<User> optionalUser = userRepository.findByName(username);

        if(optionalUser.isEmpty()) {
            return new ResponseEntity<>(new UserResponse("9999",username , "ログインしていません"), HttpStatus.UNAUTHORIZED);
        }

        User user = optionalUser.get();
        String id = user.getId().toString();

        UserResponse response = new UserResponse(id, username, "ログインしています");
        return ResponseEntity.ok(response);
    }



}