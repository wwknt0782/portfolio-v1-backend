cd ~/programs/portfolio-v1-backend

# Spring Bootビルド
./mvnw clean package

# Dockerイメージ作成
docker build -t portfolio-backend:latest .

# イメージURL
IMAGE_URL="asia-northeast1-docker.pkg.dev/portfolio-v1-514113/portfolio-repo/portfolio-backend:v2"

# タグ付け・アップロード
docker tag portfolio-backend:latest "$IMAGE_URL"
docker push "$IMAGE_URL"

# Cloud Run更新
gcloud run deploy portfolio-backend \
--image "$IMAGE_URL" \
--region asia-northeast1