package com.kwatanabe.portfoliov1backend.service;

import com.kwatanabe.portfoliov1backend.entity.User;
import com.kwatanabe.portfoliov1backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    // ユーザー名とパスワードを受け取る
    // DBと照合して一致すればJWTを返す
    // 不一致ならエラーを返す
    public String login(String userName, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Optional<User> optionalUser = userRepository.findByName(userName);

        /* todo カスタムエラー作成 */
        if(optionalUser.isEmpty()){
            String errorMessage = "一致するユーザー名が存在しません";
            log.warn(errorMessage);
            throw new Error(errorMessage);
        }

        String savedPassword = optionalUser.get().getPasswordHash();
        if(!encoder.matches(password, savedPassword)){
            String errorMessage = "パスワードが正しくありません";
            log.warn(errorMessage);
            throw new Error(errorMessage);
        }

        // JwtServiceでjwtを発行する
        return jwtService.issueToken(userName);
    }

    private String generatePasswordHash(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return encoder.encode(password);
    }
}
