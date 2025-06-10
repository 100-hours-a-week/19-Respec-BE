#!/bin/bash

set -e

echo "[INFO] Starting backend container..."

# 필요한 변수 (이 값은 GitHub Actions에서 sed 또는 envsubst로 삽입하거나 하드코딩할 수도 있음)
AWS_REGION="ap-northeast-2"
ACCOUNT_ID="123456789012"         # 실제 AWS 계정 ID로 바꾸세요
ENV="prod"                        # 예: dev, stage, prod
TAG="prod-abc1234"                # GitHub Actions에서 주입하거나 sed로 치환
REPO_NAME="specranking-backend-${ENV}"
IMAGE="${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${REPO_NAME}:${TAG}"
# ✅ start.sh (환경변수 기반 application.properties 생성 및 실행)
#!/bin/bash

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

JAR_FILE=$(find /app -name "*.jar" | head -n 1)

echo "🛑 기존 프로세스 종료 (있다면)..."
sudo fuser -k -n tcp 8080 || true
sleep 2

echo "🚀 Spring Boot 실행 중..."
echo "START: $(date)" >> "$LOG_FILE"
nohup java -jar "$JAR_FILE" --spring.config.location=file:$CONFIG_PATH >> "$LOG_FILE" 2>&1 &
# ECR 로그인
aws ecr get-login-password --region $AWS_REGION \
  | docker login --username AWS --password-stdin "${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

# 기존 컨테이너 정리
docker stop backend || true
docker rm backend || true

# Docker 이미지 pull 및 실행
docker pull $IMAGE
docker run -d --name backend -p 8080:8080 $IMAGE

echo "[INFO] Backend container started: $IMAGE"
