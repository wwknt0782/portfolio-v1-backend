# portfolio-v1-backend（バックエンド）

個人ポートフォリオサイト向けのREST APIです。Spring BootでJWT認証、お問い合わせ情報の保存、通知メール送信を実装しています。

- **公開サイト**：https://k-watanabe.com/
- **フロントエンド**：https://github.com/wwknt0782/portfolio-v1

## 主な機能

- ID・パスワードによるログイン、JWT発行・検証、ログアウト
- 認証状態・ユーザー情報の取得
- お問い合わせ内容のPostgreSQL保存、Resendによる通知メール送信

## 使用技術

| 分類 | 技術 |
| --- | --- |
| バックエンド | Java 25、Spring Boot 4.1.0、Maven |
| 認証・セキュリティ | Spring Security、JJWT、BCrypt、HttpOnly Cookie |
| DB | PostgreSQL（Neon）、Spring Data JPA、Flyway |
| API・メール | Springdoc OpenAPI、Resend |
| 実行環境 | Docker、Google Cloud Run |

## システム構成

```text
Browser
  ↓
Cloudflare Pages（Next.js / React：静的サイト）
  ↓ HTTPS / REST API
Google Cloud Run（Spring Boot）
  ├─ Neon PostgreSQL（ユーザー・お問い合わせ）
  └─ Resend（お問い合わせ通知）
```

## 設計・実装のポイント

- **認証**：Spring SecurityのフィルタでJWTを検証。JWTはHttpOnly Cookieで管理し、パスワードはBCryptでハッシュ化。
- **責務分離**：Controller / Service / Repositoryに分け、Spring Data JPAでデータアクセスを実装。
- **DB・運用**：Flywayでスキーマ変更を管理。DockerイメージをCloud Runにデプロイし、Neon・Resendと連携。

## ローカル開発

前提：Java 25、PostgreSQLへの接続情報（必要に応じてDocker）、JWT署名鍵、メール送信設定。

`application-*.properties` / `application-*.yml` に合わせて必要な環境変数を設定して起動します。

```bash
./mvnw spring-boot:run
```

## デプロイ

### ビルド

```bash
./mvnw clean package
```

### Dockerイメージ作成

```bash
docker build -t portfolio-backend:latest .
```

### タグ付け・アップロード

```bash
IMAGE_URL=<イメージURL>
docker tag portfolio-backend:latest "$IMAGE_URL"
docker push "$IMAGE_URL"
```

### Cloud Run更新

```bash
gcloud run deploy portfolio-backend \
  --image "$IMAGE_URL" \
  --region asia-northeast1
```
