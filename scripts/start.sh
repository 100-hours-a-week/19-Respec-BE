#!/bin/bash
#set -e

AWS_REGION="ap-northeast-2"
ACCOUNT_ID="115313776476"
ENV="${ENV:-prod}"
TAG="${TAG:-prod-latest}"
REPO_NAME="specranking-backend-${ENV}"
IMAGE="${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${REPO_NAME}:${TAG}"

CONFIG_PATH="/app/config/application.properties"
CONFIG_TEMPLATE_PATH="/app/config/application.properties.template"
echo "🧪 CONFIG_TEMPLATE_PATH='$CONFIG_TEMPLATE_PATH'"

LOG_FILE="/home/ec2-user/backend.log"

echo "📦 application.properties 템플릿 생성 중..."
sudo mkdir -p /app/config
sudo chown -R ec2-user:ec2-user /app/config

# echo for debug
echo "DEBUG: CONFIG_TEMPLATE_PATH is '$CONFIG_TEMPLATE_PATH'"

# 템플릿 생성 (변수 그대로 출력)
cat <<'EOF' > "$CONFIG_TEMPLATE_PATH"
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
server.port=8080
spring.profiles.active=auth, ai, no-spec-initialize, no-user-initialize, s3
spring.jwt.secret=${SPRING_JWT_SECRET}
spring.security.oauth2.client.registration.kakao.client-id=${KAKAO_CLIENT_ID}
spring.security.oauth2.client.registration.kakao.client-secret=${KAKAO_CLIENT_SECRET}
spring.security.oauth2.client.registration.kakao.redirect-uri=${BACKEND_BASE_URL}/login/oauth2/code/kakao
spring.security.oauth2.client.provider.kakao.token-uri=https://kauth.kakao.com/oauth/token
spring.cloud.aws.s3.enabled=true
cloud.aws.s3.bucket=${AWS_S3_BUCKET}
cloud.aws.region.static=${AWS_REGION}
cloud.aws.credentials.access-key=${AWS_ACCESS_KEY_ID}
cloud.aws.credentials.secret-key=${AWS_SECRET_ACCESS_KEY}
backend.base-url=${BACKEND_BASE_URL}
frontend.base-url=${FRONTEND_BASE_URL}
ai.server.url=${AI_SERVER_URL}
EOF

echo "🔁 환경변수 치환 중..."
envsubst < "$CONFIG_TEMPLATE_PATH" > "$CONFIG_PATH"

echo "✅ application.properties 생성 완료"

aws ecr get-login-password --region "$AWS_REGION" \
  | docker login --username AWS --password-stdin "${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

docker stop backend || true
docker rm backend || true

docker pull "$IMAGE"

docker run -d \
  -e SPRING_CONFIG_LOCATION=file:$CONFIG_PATH \
  -p 8080:8080 \
  -v /app/config:/app/config \
  --name backend "$IMAGE"

echo "🚀 백엔드 컨테이너 시작됨: $IMAGE"
