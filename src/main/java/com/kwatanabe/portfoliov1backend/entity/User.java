package com.kwatanabe.portfoliov1backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    // ID 自動採番
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ユーザー名
    @Column(nullable = false, length = 100)
    private String name;

    // ハッシュ化されたパスワード
    @Column(nullable = false, length = 100)
    private String passwordHash;
}
