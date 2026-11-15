# Portfolio Backend

個人ポートフォリオサイトのバックエンドAPIです。Spring Bootを使用し、ログイン認証、ユーザー情報取得、お問い合わせ受付などの機能を実装しています。

- 公開サイト: https://k-watanabe.com/
- フロントエンド: React / TypeScript（Cloudflare Pages）
- バックエンド: Java / Spring Boot（Google Cloud Run）

## 使用技術

| 分類 | 技術 |
| --- | --- |
| 言語 | Java 25 |
| フレームワーク | Spring Boot 4.1.0 |
| 認証・セキュリティ | Spring Security、JWT（JJWT）、BCrypt、HttpOnly Cookie |
| データベース | PostgreSQL（Neon）、Spring Data JPA |
| DBマイグレーション | Flyway |
| メール送信 | Resend |
| APIドキュメント | Springdoc OpenAPI |
| インフラ | Google Cloud Run、Docker |
| ビルド | Maven |

※ バージョンは2026年11月時点の `pom.xml` に基づきます。

## システム構成

```text
ブラウザ
  │
  ▼
フロントエンド（Cloudflare Pages）
  │ HTTPS / REST API
  ▼
バックエンド（Google Cloud Run / Spring Boot）
  ├── PostgreSQL（Neon）: ユーザー・お問い合わせデータ
  └── Resend: お問い合わせ通知メール
```

## 主な機能

### 認証機能

- ID・パスワードによるログイン認証
- 認証成功時にJWTを発行し、HttpOnly Cookieに設定
- 後続リクエストでJWTを検証し、認証状態を判定
- パスワードをBCryptでハッシュ化して保存
- 認証状態の確認、ユーザー情報取得、ログアウト

### お問い合わせ機能

- フォームから受信したお問い合わせ情報の受付
- PostgreSQLへの保存
- Resendを利用した通知メール送信

## 設計上の工夫

### 認証情報の取り扱い

JWTをJavaScriptから直接読み取れないHttpOnly Cookieに格納し、Spring Securityでリクエストごとの認証を処理しています。パスワードは平文で保存せず、BCryptでハッシュ化しています。

### データベース管理

Spring Data JPAを用いてデータアクセス処理を実装しています。また、Flywayを利用し、DBスキーマの変更をマイグレーションとして管理しています。

### デプロイ構成

Spring BootアプリケーションをDockerイメージとしてビルドし、Google Cloud Runで実行しています。PostgreSQLにはNeonを使用しています。

## ローカル開発

### 前提条件

- Java 25
- Docker（ローカルDBをDockerで起動する場合）
- PostgreSQLへの接続情報
- メール送信に必要な設定（お問い合わせ機能を利用する場合）

### 起動

実際の `application-*.properties` / `application-*.yml` に合わせて、DB接続情報、JWT署名鍵、メール送信設定などを環境変数で指定してください。

```bash
./mvnw spring-boot:run
```

## デプロイ
### ビルド
```bash
./mvnw clean package
```
### dockerイメージ作成
```bash
docker build -t portfolio-backend:latest .
```
### dockerイメージ作成
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

## API

認証、ユーザー情報取得、お問い合わせに関するREST APIを提供しています。正確なエンドポイント、HTTPメソッド、リクエスト・レスポンス形式はController実装とOpenAPI定義を確認して追記します。

## 関連リンク

- [公開サイト](https://k-watanabe.com/)
- [バックエンドリポジトリ](https://github.com/wwknt0782/portfolio-v1-backend)
