package com.kwatanabe.portfoliov1backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor

public class Contact {

    // ID 自動採番
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // お名前
    @Column(nullable = false, length = 100)
    private String name;

    // 会社名
    @Column(length = 100)
    private String companyName;

    // メールアドレス
    @Column(nullable = false, length = 100)
    private String email;

    // お問い合わせ内容
    @Column(columnDefinition = "TEXT", length = 2000)
    private String message;

    // フォーム送信日時
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 問い合わせ対応状態
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContactStatus status;

    // 管理者向け通知メールの送信状態
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MailStatus mailStatus;

    // メール送信失敗時のエラー内容
    @Column(columnDefinition = "TEXT")
    private String mailErrorMessage;

    // メール送信日時
    private LocalDateTime mailSentAt;

    // 最終更新日時
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (status == null) {
            status = ContactStatus.RECEIVED;
        }
        if (mailStatus == null) {
            mailStatus = MailStatus.PENDING;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
