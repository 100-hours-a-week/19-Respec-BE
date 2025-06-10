#!/bin/bash

set -e

echo "[INFO] Starting backend container..."

CONFIG_PATH=/app/config/application.properties
LOG_FILE=/home/ec2-user/backend.log

mkdir -p /app/config

echo "📦 application.properties 생성 중..."
cat <<EOF > $CONFIG_PATH
spring.datasource.url=$SPRING_DATASOURCE_URL
spring.datasource.username=$SPRING_DATASOURCE_USERNAME
spring.datasource.password=$SPRING_DATASOURCE_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
server.port=8080
spring.profiles.active=auth, ai, no-spec-initialize, no-user-initialize, s3

spring.jwt.secret=$SPRING_JWT_SECRET

spring.security.oauth2.client.registration.kakao.client-name=kakao
spring.security.oauth2.client.registration.kakao.client-id=$KAKAO_CLIENT_ID
spring.security.oauth2.client.registration.kakao.client-secret=$KAKAO_CLIENT_SECRET
spring.security.oauth2.client.registration.kakao.redirect-uri=${BACKEND_BASE_URL}/login/oauth2/code/kakao
spring.security.oauth2.client.registration.kakao.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.kakao.client-authentication-method=client_secret_post

spring.security.oauth2.client.provider.kakao.authorization-uri=https://kauth.kakao.com/oauth/authorize
spring.security.oauth2.client.provider.kakao.token-uri=https://kauth.kakao.com/oauth/token
spring.security.oauth2.client.provider.kakao.user-info-uri=https://kapi.kakao.com/v2/user/me
spring.security.oauth2.client.provider.kakao.user-name-attribute=id

spring.cloud.aws.s3.enabled=true
spring.cloud.aws.stack.auto=false
cloud.aws.s3.bucket=$AWS_S3_BUCKET
cloud.aws.region.static=$AWS_REGION
cloud.aws.credentials.access-key=$AWS_ACCESS_KEY_ID
cloud.aws.credentials.secret-key=$AWS_SECRET_ACCESS_KEY

logging.level.org.springframework.security.oauth2.client=DEBUG
logging.level.org.springframework.web.client.RestTemplate=DEBUG
mock.login.user=false

backend.base-url=$BACKEND_BASE_URL
frontend.base-url=$FRONTEND_BASE_URL
frontend.redirect-url=${FRONTEND_BASE_URL}/oauth-redirect
spring.graphql.cors.allowed-origins=${FRONTEND_BASE_URL}
ai.server.url=$AI_SERVER_URL
ai.server.url.path=/spec/v1/post
EOF

echo "✅ application.properties 생성 완료"

IMAGE="${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${REPO_NAME}:${TAG}"

# ECR 로그인
aws ecr get-login-password --region $AWS_REGION \
  | docker login --username AWS --password-stdin "${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

# 기존 컨테이너 정리
docker stop backend || true
docker rm backend || true

# Docker 이미지 pull 및 실행
docker pull "$IMAGE"
docker run -d --name backend -p 8080:8080 \
  -e SPRING_DATASOURCE_URL \
  -e SPRING_DATASOURCE_USERNAME \
  -e SPRING_DATASOURCE_PASSWORD \
  -e SPRING_JWT_SECRET \
  -e KAKAO_CLIENT_ID \
  -e KAKAO_CLIENT_SECRET \
  -e BACKEND_BASE_URL \
  -e FRONTEND_BASE_URL \
  -e AI_SERVER_URL \
  -e AWS_S3_BUCKET \
  -e AWS_REGION \
  -e AWS_ACCESS_KEY_ID \
  -e AWS_SECRET_ACCESS_KEY \
  -e ACCOUNT_ID \
  -e REPO_NAME \
  -e TAG \
  -v /app/config:/app/config \
  "$IMAGE"

echo "[INFO] Backend container started with tag: $TAG"
